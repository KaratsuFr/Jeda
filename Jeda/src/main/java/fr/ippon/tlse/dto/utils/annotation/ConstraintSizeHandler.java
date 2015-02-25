package fr.ippon.tlse.dto.utils.annotation;

import java.lang.annotation.Annotation;

import javax.validation.constraints.Size;

import fr.ippon.tlse.annotation.AnnotationTypeHandler;
import fr.ippon.tlse.dto.FieldDto;
import fr.ippon.tlse.dto.KeyValueDto;
import fr.ippon.tlse.dto.utils.AnnotationHandler;

@AnnotationTypeHandler(annoClass = Size.class)
public class ConstraintSizeHandler implements AnnotationHandler {

	@Override
	public FieldDto handleAnnotation(Annotation anno, FieldDto fDto) {
		Size annotation = (Size) anno;
		KeyValueDto min = new KeyValueDto("min", String.valueOf(annotation.min()));
		fDto.getLstConstraint().add(min);

		if (annotation.max() != Integer.MAX_VALUE) {
			KeyValueDto max = new KeyValueDto("max", String.valueOf(annotation.max()));
			fDto.getLstConstraint().add(max);

		}

		return null;
	}

}
