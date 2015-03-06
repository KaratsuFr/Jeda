package fr.ippon.tlse.dto.utils;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath.ClassInfo;

import fr.ippon.tlse.ApplicationUtils;
import fr.ippon.tlse.annotation.AnnotationHandler;
import fr.ippon.tlse.annotation.AnnotationTypeHandler;
import fr.ippon.tlse.dto.exception.ErrorCode;
import fr.ippon.tlse.dto.exception.JedaException;

@Slf4j
public class AnnotationManager {

	public final static AnnotationManager						SINGLETON	= new AnnotationManager();

	@Getter
	private Map<Class<? extends Annotation>, AnnotationHandler>	mapAnnoH	= new HashMap<>();

	private AnnotationManager() {

		ImmutableSet<ClassInfo> lstClassAnntation = ApplicationUtils.SINGLETON.getClassP().getTopLevelClassesRecursive(
				ApplicationUtils.SINGLETON.getBasePackage());
		try {
			for (ClassInfo classInfo : lstClassAnntation) {
				AnnotationTypeHandler annoTypeH = classInfo.load().getAnnotation(AnnotationTypeHandler.class);
				if (annoTypeH != null) {
					mapAnnoH.put(annoTypeH.annoClass(), ((AnnotationHandler) classInfo.load().newInstance()));
				} else {
					// generic anno handler

				}
			}
		} catch (ClassCastException | InstantiationException | IllegalAccessException e) {
			String msg = "Init failed due to Invalid Annotation Hander - class must implement "
					+ AnnotationHandler.class.getName() + " and have a public default constructor.";
			log.error(msg, e);
			throw new JedaException(ErrorCode.TO_BE_DEFINE, msg);
		}
	}
}
