package fr.ippon.tlse.dto.utils;

import java.lang.annotation.Annotation;

import fr.ippon.tlse.dto.FieldDto;

public interface AnnotationHandler {
	FieldDto handleAnnotation(Annotation annotation, FieldDto fDto);
}
