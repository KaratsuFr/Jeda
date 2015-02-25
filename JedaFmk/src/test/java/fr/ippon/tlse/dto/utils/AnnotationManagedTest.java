package fr.ippon.tlse.dto.utils;

import java.lang.annotation.Annotation;
import java.util.Map;

import javax.validation.constraints.Size;

import org.testng.Assert;
import org.testng.annotations.Test;

import fr.ippon.tlse.dto.utils.annotation.ConstraintSizeHandler;

public class AnnotationManagedTest {

	@Test
	public void AnnotationManagedCheckInit() {

		Map<Class<? extends Annotation>, AnnotationHandler> mapAnno = AnnotationManager.SINGLETON.getMapAnnoH();

		Assert.assertNotNull(mapAnno);
		AnnotationHandler annoH = mapAnno.get(Size.class);
		Assert.assertNotNull(annoH);
		Assert.assertEquals(annoH.getClass(), ConstraintSizeHandler.class);

	}
}
