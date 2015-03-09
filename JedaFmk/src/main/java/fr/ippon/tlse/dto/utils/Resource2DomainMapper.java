package fr.ippon.tlse.dto.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TimeZone;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

import fr.ippon.tlse.ApplicationUtils;
import fr.ippon.tlse.annotation.Embended;
import fr.ippon.tlse.annotation.Id;
import fr.ippon.tlse.dto.FieldDto;
import fr.ippon.tlse.dto.ResourceDto;
import fr.ippon.tlse.dto.ValueDto;
import fr.ippon.tlse.dto.exception.ErrorCode;
import fr.ippon.tlse.dto.exception.JedaException;

public enum Resource2DomainMapper {
	SINGLETON;

	public <T> List<T> buildLstDomainFromResource(ResourceDto resource, Class<T> targetClass) {
		if (resource == null) {
			return null;
		}

		if (!StringUtils.equals(resource.getClassName(), targetClass.getSimpleName())) {
			throw new JedaException(ErrorCode.TO_BE_DEFINE,
					"Can t build domain from Resource, type doesn't match. Expected:" + targetClass.getSimpleName()
							+ " but Resource is made of:" + resource.getClassName());
		}

		List<T> lstDomain = new ArrayList<>();
		try {
			for (List<ValueDto> lstValueField : resource.getLstValues()) {
				T domainBean = targetClass.newInstance();
				if (resource.getLstFieldInfo().size() != lstValueField.size()) {
					throw new JedaException(ErrorCode.TO_BE_DEFINE,
							"Invalid Resource - fieldInfo and number of value must match. \n" + resource.toString());
				}
				for (int i = 0; i < resource.getLstFieldInfo().size(); i++) {
					FieldDto fieldDto = resource.getLstFieldInfo().get(i);
					ValueDto valDto = lstValueField.get(i);
					if (valDto.getErrorCode() != null) {
						throw new JedaException(ErrorCode.TO_BE_DEFINE,
								"Invalid resource Object, it should not contain Error.");
					}
					Field f = targetClass.getDeclaredField(fieldDto.getFieldName());
					f.setAccessible(true);
					String className = fieldDto.getJavaType();
					if (className.contains("<")) {
						className = StringUtils.left(className, className.indexOf("<"));
					}

					Class<?> valueType = Class.forName(className);
					Object val = valDto.getValue();
					if (val != null) {
						// special case for Collection & Map
						if (Collection.class.isAssignableFrom(valueType)) {
							val = convertCollectionTypeToSpecific(val, fieldDto, valDto, f);
						} else if (Map.class.isAssignableFrom(valueType)) {
							val = convertMapTypeToSpecific(val, fieldDto.getJavaType(), fieldDto, valDto, f);
						} else if (StringUtils.startsWith(fieldDto.getJavaType(),
								ApplicationUtils.SINGLETON.getDomainPackage())
								|| StringUtils.startsWith(fieldDto.getJavaType(),
										ApplicationUtils.SINGLETON.getCustomDomainPackage())) {
							val = convertDomainTypeToSpecific(val, fieldDto, valDto, f);
						} else if (!valueType.isAssignableFrom(val.getClass())) {
							val = convertRawTypeToSpecific(val, valueType, fieldDto, valDto);
						}
					}
					f.set(domainBean, val);
				}
				lstDomain.add(domainBean);
			}
		} catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchFieldException
				| SecurityException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
			throw new JedaException(ErrorCode.TO_BE_DEFINE, String.format(
					"Resource [%s] to Domain impossible, fieldInfo doesn't match target %s", resource, targetClass), e);

		}
		return lstDomain;
	}

	private <Z> Object convertMapTypeToSpecific(Object genericVal, String targetClass, FieldDto fDto, ValueDto valDto,
			Field f) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {

		throw new NotImplementedException(String.format(
				"convertMapTypeToSpecific from %s to %s - for FieldDto %s and valDto %s", genericVal.getClass(),
				targetClass, fDto, valDto));
	}

	private <Z> Object convertDomainTypeToSpecific(Object genericVal, FieldDto fDto, ValueDto valDto, Field f2)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchFieldException,
			SecurityException {
		Embended annoEmbended = f2.getAnnotation(Embended.class);
		if (annoEmbended == null || annoEmbended.value() == false) {
			return null;
		} else {
			@SuppressWarnings("unchecked")
			Map<String, Object> colVal = (Map<String, Object>) genericVal;

			Object targetObj = f2.getType().newInstance();
			for (Entry<String, Object> entry : colVal.entrySet()) {
				Field f = null;
				// special case for @Id
				if (StringUtils.equals("_id", entry.getKey())) {
					// search field is @Id
					Field[] tabF = f2.getType().getDeclaredFields();
					for (Field field : tabF) {
						Id annoId = field.getAnnotation(Id.class);
						if (annoId != null) {
							f = field;
						}
					}
				} else {
					f = f2.getType().getDeclaredField(entry.getKey());
				}
				f.setAccessible(true);
				f.set(targetObj, entry.getValue());
			}
			return targetObj;
		}

	}

	private <Z> Object convertCollectionTypeToSpecific(Object genericVal, FieldDto fDto, ValueDto valDto, Field f2)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchFieldException,
			SecurityException {
		Embended annoEmbended = f2.getAnnotation(Embended.class);
		if (annoEmbended == null || annoEmbended.value() == false) {
			return null;
		} else {
			@SuppressWarnings("unchecked")
			Collection<Map<String, Object>> colVal = (Collection<Map<String, Object>>) genericVal;

			String collInnerClassName = StringUtils.substringBetween(fDto.getJavaType(), "<", ">");
			Class<?> collInnerClass = Class.forName(collInnerClassName);
			List<Object> lst = new ArrayList<>();
			for (Map<String, Object> mappedValue : colVal) {
				Object targetObj = collInnerClass.newInstance();
				for (Entry<String, Object> entry : mappedValue.entrySet()) {
					Field f = collInnerClass.getDeclaredField(entry.getKey());
					f.setAccessible(true);
					f.set(targetObj, entry.getValue());
				}
				lst.add(targetObj);
			}
			return lst;
		}

	}

	private <Z> Object convertRawTypeToSpecific(Object genericVal, Class<Z> targetClass, FieldDto fDto, ValueDto valDto)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {

		if (genericVal == null) {
			return Optional.empty();
		}
		if (targetClass.equals(Optional.class)) {
			return Optional.of(genericVal);
		}

		if (targetClass.equals(Character.class) && genericVal.getClass().equals(String.class)) {
			String strVal = ((String) genericVal);
			if (StringUtils.isBlank(strVal)) {
				return null;
			} else {
				return strVal.charAt(0);
			}
		}

		if (targetClass.equals(Date.class) && genericVal.getClass().equals(Long.class)) {
			Long dateInMs = ((Long) genericVal);
			return new Date(dateInMs);
		}
		if (targetClass.equals(Calendar.class) && genericVal.getClass().equals(Long.class)) {
			Long dateInMs = ((Long) genericVal);
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			cal.setTimeInMillis(dateInMs);
			return cal;
		}
		if (targetClass.equals(LocalDate.class)) {
			@SuppressWarnings("unchecked")
			List<Integer> lstAttrDate = ((List<Integer>) genericVal);
			LocalDate lDate = LocalDate.of(lstAttrDate.get(0), lstAttrDate.get(1), lstAttrDate.get(2));
			return lDate;
		}
		if (targetClass.equals(LocalDateTime.class)) {
			@SuppressWarnings("unchecked")
			List<Integer> lstAttrDate = ((List<Integer>) genericVal);
			LocalDateTime lDate = LocalDateTime.of(lstAttrDate.get(0), lstAttrDate.get(1), lstAttrDate.get(2),
					lstAttrDate.get(3), lstAttrDate.get(4), lstAttrDate.get(5), lstAttrDate.get(6));
			return lDate;
		}

		if (Number.class.isAssignableFrom(genericVal.getClass())) {
			Number num = ((Number) genericVal);
			return primiviteNumberToObjectNumber(targetClass, num);
		}
		throw new NotImplementedException(String.format(
				"convertRawTypeToSpecific from %s to %s - for FieldDto %s and valDto %s", genericVal.getClass(),
				targetClass, fDto, valDto));
	}

	private <Z> Object primiviteNumberToObjectNumber(Class<Z> targetClass, Number intVal) {
		if (targetClass.equals(Byte.class)) {
			return intVal.byteValue();
		}
		if (targetClass.equals(Short.class)) {
			return intVal.shortValue();
		}
		if (targetClass.equals(Long.class)) {
			return intVal.longValue();
		}
		if (targetClass.equals(Float.class)) {
			return intVal.floatValue();
		}
		if (targetClass.equals(Double.class)) {
			return intVal.doubleValue();
		}
		return "";
	}

}
