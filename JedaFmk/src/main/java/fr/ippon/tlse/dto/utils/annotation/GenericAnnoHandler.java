package fr.ippon.tlse.dto.utils.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import lombok.extern.slf4j.Slf4j;
import fr.ippon.tlse.annotation.AnnotationHandler;
import fr.ippon.tlse.dto.FieldDto;

@Slf4j
public class GenericAnnoHandler implements AnnotationHandler {

	@Override
	public FieldDto handleAnnotation(Annotation anno, FieldDto fDto) {
		String annotationName = anno.annotationType().getSimpleName();
		String fieldName = Character.toLowerCase(annotationName.charAt(0)) + annotationName.substring(1);

		try {
			Field field = fDto.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			Method m = anno.getClass().getMethod("value");
			if (m != null) {
				field.set(fDto, m.invoke(anno));
			}
		} catch (IllegalAccessException | SecurityException | NoSuchFieldException | NoSuchMethodException
				| InvocationTargetException e) {
			log.debug("Impossible to handle annotation {} - Details: {}", anno, e.getMessage());
		}

		return fDto;
	}
}
