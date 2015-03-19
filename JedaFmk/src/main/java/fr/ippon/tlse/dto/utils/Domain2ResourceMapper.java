package fr.ippon.tlse.dto.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import fr.ippon.tlse.ApplicationConfig;
import fr.ippon.tlse.ApplicationUtils;
import fr.ippon.tlse.annotation.AnnotationHandler;
import fr.ippon.tlse.annotation.Description;
import fr.ippon.tlse.annotation.Domain;
import fr.ippon.tlse.annotation.Embended;
import fr.ippon.tlse.dto.FieldDto;
import fr.ippon.tlse.dto.ResourceDto;
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
																								Class<?> classObject)
																								throws JsonProcessingException {
																							return buildFieldInfo(classObject);
																						}
																					});

	public void resetCache() {
		cacheClassToFieldDto.invalidateAll();
	}

	private Validator	validator	= Validation.buildDefaultValidatorFactory().getValidator();

	public <T> ResourceDto buildResourceFromDomain(List<T> lstDomainValues, Class<T> targetClass, boolean validation) {

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

		// FIXME
		res.setLstDomain(ApplicationConfig.getMapper().valueToTree(lstDomainValues));
		res.setTotalNbResult(lstDomainValues.size());

		// finally check validation error on object
		if (validation) {
			Set<ConstraintViolation<Object>> setConstraintViolations = validator.validate(lstDomainValues);
			for (ConstraintViolation<Object> constraintViolation : setConstraintViolations) {
				res.getLstErrorCodes().add(constraintViolation.getMessage());
			}
		}
		//

		return res;
	}

	private GenericAnnoHandler	genericAnnoH	= new GenericAnnoHandler();

	private <T> List<FieldDto> buildFieldInfo(Class<T> domainClass) throws JsonProcessingException {
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
				} else if (Collection.class.isAssignableFrom(field.getType())) {
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
				fieldDto.setJsName(field.getName());

				Annotation[] annotationOfField = field.getAnnotations();
				for (Annotation annotation : annotationOfField) {
					AnnotationHandler annoH = AnnotationManager.SINGLETON.getMapAnnoH()
							.get(annotation.annotationType());
					if (annoH != null) {
						fieldDto = annoH.handleAnnotation(annotation, fieldDto);
					} else {
						fieldDto = genericAnnoH.handleAnnotation(annotation, fieldDto);
					}
					if (JsonProperty.class.isAssignableFrom(annotation.annotationType())) {
						fieldDto.setJsName(((JsonProperty) annotation).value());
					}
				}

				fieldDto.setJsType(ConvertJs2JavaType.getJsTypeFromClass(javaType));
				Embended annoEmbended = field.getAnnotation(Embended.class);
				if (annoEmbended != null && annoEmbended.value()) {
					// domain bean only can embended
					if (StringUtils.contains(javaType.getName(), ApplicationUtils.SINGLETON.getCustomDomainPackage())
							|| StringUtils.contains(javaType.getName(), ApplicationUtils.SINGLETON.getDomainPackage())) {

						List<FieldDto> lstFieldDto = buildFieldInfo(javaType);
						fieldDto.setEmbendedType(lstFieldDto);
					}
				}
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
