package fr.ippon.tlse.dto.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.ws.rs.NotSupportedException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import fr.ippon.tlse.ApplicationUtils;
import fr.ippon.tlse.annotation.Description;
import fr.ippon.tlse.annotation.Domain;
import fr.ippon.tlse.annotation.Embended;
import fr.ippon.tlse.dto.FieldDto;
import fr.ippon.tlse.dto.ResourceDto;
import fr.ippon.tlse.dto.ValueDto;
import fr.ippon.tlse.dto.exception.ErrorCode;
import fr.ippon.tlse.dto.exception.InvalidBeanException;
import fr.ippon.tlse.dto.exception.JedaException;
import fr.ippon.tlse.dto.utils.annotation.GenericAnnoHandler;

@Slf4j
public enum Domain2ResourceMapper {
	SINGLETON;

	@Getter(value = AccessLevel.PRIVATE)
	private LoadingCache<Class<?>, List<FieldDto>>	cacheClassToFieldDto	= CacheBuilder
																					.newBuilder()
																					.maximumSize(1000)
																					.build(new CacheLoader<Class<?>, List<FieldDto>>()
																					{
																						@Override
																						public List<FieldDto> load(
																								Class<?> classObject) {
																							return buildFieldInfo(classObject);
																						}
																					});

	public void resetCache() {
		cacheClassToFieldDto.invalidateAll();
	}

	public <T> ResourceDto buildResourceFromDomain(List<T> lstDomainValues, Class<T> targetClass) {
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

		Class<T> classOfItemInList = targetClass;
		// 1 check package lstDomainValues
		if (!(StringUtils.contains(classOfItemInList.getPackage().getName(),
				ApplicationUtils.SINGLETON.getDomainPackage()) || StringUtils.contains(classOfItemInList.getPackage()
				.getName(), ApplicationUtils.SINGLETON.getCustomDomainPackage()))) {
			throw new InvalidBeanException(classOfItemInList);
		}
		Domain domainAnno = classOfItemInList.getAnnotation(Domain.class);
		if (domainAnno == null) {
			throw new InvalidBeanException(classOfItemInList);

		}
		ResourceDto res = new ResourceDto();
		res.setClassName(classOfItemInList.getSimpleName());

		// FIXME : determine logical name for Domain Class & get Description
		res.setLabel(domainAnno.label());

		// 2 build field info
		res.setLstFieldInfo(getCacheClassToFieldDto().getUnchecked(classOfItemInList));

		Description annoDescrip = classOfItemInList.getAnnotation(Description.class);
		if (annoDescrip != null) {
			res.setDescription(annoDescrip.value());
		}

		// 3 build Resource from domain value
		try {
			for (Object object : lstDomainValues) {

				Map<String, ValueDto> indexFielInfo = new HashMap<>();

				List<FieldDto> lstFieldDto = res.getLstFieldInfo();
				List<ValueDto> lstValue = new ArrayList<>();
				for (FieldDto fieldDto : lstFieldDto) {
					Field field = object.getClass().getDeclaredField(fieldDto.getFieldName());
					field.setAccessible(true);

					// save index of id column
					if (fieldDto.isId()) {
						res.setPositionOfId(lstValue.size());

					}
					ValueDto vDto = new ValueDto();
					// Collection or Domain object are not mapped by default need @Embended
					if (Collection.class.isAssignableFrom(field.getType())) {
						Embended annoEmbended = field.getAnnotation(Embended.class);
						if (annoEmbended == null || annoEmbended.value() == false) {
							vDto.setUrlResourceMapping(field.getType().getSimpleName());
						} else {
							vDto.setValue(field.get(object));
						}
					} else if (StringUtils.startsWith(fieldDto.getJavaType(),
							ApplicationUtils.SINGLETON.getDomainPackage())
							|| StringUtils.startsWith(fieldDto.getJavaType(),
									ApplicationUtils.SINGLETON.getCustomDomainPackage())) {
						Embended annoEmbended = field.getAnnotation(Embended.class);
						if (annoEmbended == null || annoEmbended.value() == false) {
							String id = findIdOfTargetValue(field);
							vDto.setUrlResourceMapping(String.format("%s?id=%s", field.getType().getSimpleName(), id));
						} else {
							vDto.setValue(field.get(object));
						}
					} else if (field.getType().isEnum()) {
						throw new NotSupportedException("Enum on Domain bean are not supported!");
					} else if (Map.class.isAssignableFrom(field.getType())) {
						throw new NotSupportedException("Map on Domain bean are not supported!");
					} else if (StringUtils.startsWith(fieldDto.getJavaType(), "java.")) {
						vDto.setValue(field.get(object));
					} else if (StringUtils.equals(ObjectId.class.getName(), fieldDto.getJavaType())) {
						ObjectId objId = (ObjectId) field.get(object);

						vDto.setValue(objId);
					} else {
						throw new NotSupportedException(field.getType().getSimpleName()
								+ " on Domain bean are not supported!");
					}
					indexFielInfo.put(field.getName(), vDto);
					lstValue.add(vDto);
				}
				res.getLstValues().add(lstValue);
				res.setTotalNbResult(lstValue.size());
				// 5 finally check validation error on object
				Set<ConstraintViolation<Object>> setConstraintViolations = validator.validate(object);
				for (ConstraintViolation<Object> constraintViolation : setConstraintViolations) {
					String propPath = constraintViolation.getPropertyPath().toString();
					ValueDto valDto = indexFielInfo.get(propPath);

					// if propPath match indeFielInfo : the constraint match a field of the object .. we set the error to corresponding value position
					if (valDto != null) {
						valDto.setErrorCode(constraintViolation.getMessage());
					} else {
						res.getLstErrorCodes().add(constraintViolation.getMessage());
					}
				}

				// FIXME 6 sort data by group and order

			}
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			// can't happen : introspection wins
			String msg = "BUG - this statement should never happen!";
			log.error(msg, e);
			throw new JedaException(ErrorCode.TO_BE_DEFINE, e);
		}

		return res;
	}

	private String findIdOfTargetValue(Field field) {
		// FIXME TODO
		return "";
	}

	private GenericAnnoHandler	genericAnnoH	= new GenericAnnoHandler();

	private <T> List<FieldDto> buildFieldInfo(Class<T> domainClass) {
		log.trace("buildFieldInfo for class {}", domainClass);

		List<FieldDto> lstField = new ArrayList<FieldDto>();

		Field[] tabFielsOfClass = domainClass.getDeclaredFields();
		Set<Integer> setOrderReserved = new HashSet<>();
		for (Field field : tabFielsOfClass) {
			JsonIgnore annoIgnore = field.getAnnotation(JsonIgnore.class);
			Class<?> javaType = null;
			if (annoIgnore == null) {
				FieldDto fieldDto = new FieldDto();
				fieldDto.setLabel(field.getName());
				if (field.getType().isPrimitive()) {
					String primType = field.getType().getName();
					switch (primType) {
						case "boolean":
							javaType = Boolean.class;
							break;
						case "char":
							javaType = Character.class;
							break;
						case "byte":
							javaType = Byte.class;
							break;
						case "short":
							javaType = Short.class;
							break;
						case "int":
							javaType = Integer.class;
							break;
						case "long":
							javaType = Long.class;
							break;
						case "float":
							javaType = Float.class;
							break;
						case "double":
							javaType = Double.class;
							break;
						default:
							throw new JedaException(ErrorCode.TO_BE_DEFINE, "Unmanaged primitive type: " + primType);
					}
				} else if (Collection.class.isAssignableFrom(field.getType())
						|| Map.class.isAssignableFrom(field.getType())) {

					Type type = field.getGenericType();
					if (type instanceof ParameterizedType) {
						javaType = ((ParameterizedType) type).getActualTypeArguments()[0].getClass();
					} else {
						javaType = field.getType();
					}
				} else {
					javaType = field.getType();
				}

				fieldDto.setJavaType(javaType.getName());
				fieldDto.setFieldName(field.getName());
				Annotation[] annotationOfField = field.getAnnotations();
				for (Annotation annotation : annotationOfField) {
					AnnotationHandler annoH = AnnotationManager.SINGLETON.getMapAnnoH()
							.get(annotation.annotationType());
					if (annoH != null) {
						fieldDto = annoH.handleAnnotation(annotation, fieldDto);
					} else {
						fieldDto = genericAnnoH.handleAnnotation(annotation, fieldDto);
					}
				}
				fieldDto.setJsType(ConvertJs2JavaType.getJsTypeFromClass(javaType));
				setOrderReserved.add(fieldDto.getOrder());
				lstField.add(fieldDto);
			}
		}

		// FIXME sort & recalculate missing order values
		int currOrder = 0;
		for (FieldDto fDto : lstField) {
			while (setOrderReserved.contains(currOrder)) {
				currOrder++;
			}
			setOrderReserved.add(currOrder);
			fDto.setOrder(currOrder);
		}
		return lstField;
	}
}
