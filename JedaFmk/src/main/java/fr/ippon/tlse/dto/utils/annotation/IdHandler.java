package fr.ippon.tlse.dto.utils.annotation;

import java.lang.annotation.Annotation;

import fr.ippon.tlse.annotation.AnnotationHandler;
import fr.ippon.tlse.annotation.AnnotationTypeHandler;
import fr.ippon.tlse.annotation.Id;
import fr.ippon.tlse.dto.FieldDto;

@AnnotationTypeHandler(annoClass = Id.class)
public class IdHandler implements AnnotationHandler {

	@Override
	public FieldDto handleAnnotation(Annotation anno, FieldDto fDto) {
		fDto.setId(true);
		return fDto;
	}

}
