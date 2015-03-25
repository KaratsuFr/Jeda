package fr.ippon.tlse.dto.utils;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

public enum ConvertJs2JavaType {

	TEXT(String.class, "text"), BOOL(Boolean.class, "checkbox"), DATE(java.util.Date.class, "date"), NUMBER(
			Number.class, "number"), LINK(URI.class, "link");

	@Getter
	private Class<?>						javaClass;

	@Getter
	private String							jsType;

	private static boolean					isMapInit	= false;
	private static Map<Class<?>, String>	mapJavaToJs	= new HashMap<>();
	private static Map<String, Class<?>>	mapJsToJava	= new HashMap<>();

	private static void initMapConvert() {
		if (!isMapInit) {
			isMapInit = true;
			ConvertJs2JavaType[] possibleConverter = ConvertJs2JavaType.values();
			for (ConvertJs2JavaType convertJs2JavaType : possibleConverter) {
				mapJavaToJs.put(convertJs2JavaType.javaClass, convertJs2JavaType.jsType);
				mapJsToJava.put(convertJs2JavaType.jsType, convertJs2JavaType.javaClass);
			}
		}
	}

	private ConvertJs2JavaType(Class<?> javaC, String jsT) {
		javaClass = javaC;
		jsType = jsT;
	}

	public static String getJsTypeFromClass(Class<?> javaClass) {
		initMapConvert();
		Class<?> searchClass = javaClass;
		if (Number.class.isAssignableFrom(searchClass)) {
			searchClass = Number.class;
		}
		String jsType = mapJavaToJs.get(searchClass);
		// SET DEFAULT TYPE TO TEXT
		if (jsType == null) {
			jsType = TEXT.jsType;
		}
		return jsType;
	}

}
