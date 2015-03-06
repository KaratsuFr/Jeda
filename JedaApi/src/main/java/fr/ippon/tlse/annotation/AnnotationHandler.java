package fr.ippon.tlse.annotation;

import java.lang.annotation.Annotation;

import fr.ippon.tlse.dto.FieldDto;

public interface AnnotationHandler {
	FieldDto handleAnnotation(Annotation annotation, FieldDto fDto);
}
