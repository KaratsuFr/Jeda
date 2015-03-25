package fr.ippon.tlse.dto.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.ws.rs.core.Link;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
		if (!(isDomainBean(classOfItemInList))) {
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
		// Set<String> lstPropertiesNotSerialized = res.getLstFieldInfo().stream()
		// .filter(f -> ConvertJs2JavaType.LINK.getJsType().equals(f.getJsType())).map(FieldDto::getJsName)
		// .collect(Collectors.toSet());

		ArrayNode jsonDomainNode = ApplicationConfig.getMapper().valueToTree(lstDomainValues);
		// replace comlexe type not embended by links
		try {
			for (FieldDto field : res.getLstFieldInfo()) {
				if (field.isLink()) {
					for (int i = 0; i < jsonDomainNode.size(); i++) {
						ObjectNode masterNode = (ObjectNode) jsonDomainNode.get(i);
						for (Iterator<Entry<String, JsonNode>> nodeElements = masterNode.fields(); nodeElements
								.hasNext();) {
							Entry<String, JsonNode> entry = nodeElements.next();
							if (entry.getKey().equals(field.getJsName())) {
								JsonNode node = entry.getValue();

								// JsonNode node = jsonDomainNode.findValue(field.getJsName());

								if (!node.isNull()) {
									if (ObjectNode.class.isAssignableFrom(node.getClass())) {
										ObjectNode objNode = (ObjectNode) node;
										// add _id
										Link link = ApplicationUtils.SINGLETON.buildLinkFromDomainClass(
												Class.forName(field.getJavaType()),
												Optional.of(objNode.get("_id").asText()));
										if (link != null) {
											entry.setValue(buildNodeFromLink("", link));
										}
									} else if (ArrayNode.class.isAssignableFrom(node.getClass())) {
										Link link = ApplicationUtils.SINGLETON.buildLinkFromDomainClass(Class
												.forName(field.getJavaType()));

										if (link != null) {
											entry.setValue(buildNodeFromLink(".List", link));
										}
									}
								}
							}

						}
					}

				}
			}
		} catch (ClassNotFoundException e) {
			log.warn("Unable to build link from type.", e);
		}
		res.setLstDomain(jsonDomainNode);

		res.setTotalNbResult(lstDomainValues.size());

		// finally check validation error on object
		if (validation) {
			Set<ConstraintViolation<Object>> setConstraintViolations = validator.validate(lstDomainValues);
			res.setLstErrorCodes(setConstraintViolations.stream().map(ConstraintViolation::getMessage)
					.collect(Collectors.toList()));
		}
		//
		return res;
	}

	private JsonNode buildNodeFromLink(String suffix, Link... link) {
		List<Link> lstLink = Arrays.asList(link);
		ObjectNode nodeLinks = ApplicationConfig.getMapper().createObjectNode();
		ArrayNode nodeArrayLink = ApplicationConfig.getMapper().createArrayNode();
		nodeLinks.set("links", nodeArrayLink);
		lstLink.forEach(new Consumer<Link>()
		{

			@Override
			public void accept(Link l) {
				ObjectNode oneLink = ApplicationConfig.getMapper().createObjectNode();
				oneLink.put("rel", l.getRel() + suffix);
				oneLink.put("href", l.getUri().toString());
				nodeArrayLink.add(oneLink);
			}
		});

		return nodeLinks;
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
					if (!updateEmbendedType(field, fieldDto)) {
						fieldDto.setLink(true);
					}
					Type type = field.getGenericType();
					if (type instanceof ParameterizedType) {
						try {
							javaType = Class.forName(((ParameterizedType) type).getActualTypeArguments()[0]
									.getTypeName());
						} catch (ClassNotFoundException e) {
							throw new JedaException(ErrorCode.TO_BE_DEFINE, "Unable to determine type ", e);

						}
					} else {
						javaType = field.getType();
					}
				} else if (isDomainBean(field.getType())) {
					javaType = field.getType();
					if (!updateEmbendedType(field, fieldDto)) {
						fieldDto.setLink(true);
					}
				} else {
					javaType = field.getType();
				}

				fieldDto.setJavaType(javaType.getName());
				fieldDto.setFieldName(field.getName());
				fieldDto.setJsName(field.getName());
				fieldDto.setJsType(ConvertJs2JavaType.getJsTypeFromClass(javaType));
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

	private boolean isDomainBean(Class<?> beanClass) {
		return isDomainBean(beanClass.getName());
	}

	private boolean isDomainBean(String beanClassName) {
		return StringUtils.contains(beanClassName, ApplicationUtils.SINGLETON.getCustomDomainPackage())
				|| StringUtils.contains(beanClassName, ApplicationUtils.SINGLETON.getDomainPackage());
	}

	private boolean updateEmbendedType(Field field, FieldDto fieldDto) throws JsonProcessingException {
		Embended annoEmbended = field.getAnnotation(Embended.class);
		boolean isEmbended = false;
		if (annoEmbended != null && annoEmbended.value()) {
			// domain bean only can embended
			if (isDomainBean(field.getType())) {
				isEmbended = true;
				List<FieldDto> lstFieldDto = getCacheClassToFieldDto().getUnchecked(field.getType());
				fieldDto.setEmbendedType(lstFieldDto);
			}
		}
		return isEmbended;
	}
}
