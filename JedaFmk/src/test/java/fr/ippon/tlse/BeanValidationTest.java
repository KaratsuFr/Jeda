package fr.ippon.tlse;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.testng.Assert;
import org.testng.annotations.Test;

@Slf4j
public class BeanValidationTest {

	@Data
	private class DataTest {
		@Size(min = 2, max = 10)
		private String	txt;
	}

	@Test
	public void testBeanValidation() {
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		DataTest data = new DataTest();
		data.setTxt("45645646500000000000000000006");
		Set<ConstraintViolation<DataTest>> constraints = validator.validate(data);
		log.debug(constraints.toString());
		Assert.assertEquals(constraints.size(), 1);
		for (ConstraintViolation<DataTest> constraintViolation : constraints) {
			Assert.assertEquals(constraintViolation.getPropertyPath().toString(), "txt");
		}
	}
}
