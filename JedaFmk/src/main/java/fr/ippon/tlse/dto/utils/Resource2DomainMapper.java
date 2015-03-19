package fr.ippon.tlse.dto.utils;

import java.io.IOException;
import java.util.List;

import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.type.TypeFactory;

import fr.ippon.tlse.ApplicationConfig;
import fr.ippon.tlse.dto.ResourceDto;
import fr.ippon.tlse.dto.exception.ErrorCode;
import fr.ippon.tlse.dto.exception.JedaException;

public enum Resource2DomainMapper {
	SINGLETON;

	public <T> List<T> buildLstDomainFromResource(ResourceDto resource, Class<T> targetClass) {
		if (resource == null) {
			return null;
		}

		if (!StringUtils.equals(resource.getClassName(), targetClass.getSimpleName())) {
			throw new JedaException(ErrorCode.TO_BE_DEFINE,
					"Can t build domain from Resource, type doesn't match. Expected:" + targetClass.getSimpleName()
							+ " but Resource is made of:" + resource.getClassName());
		}

		List<T> lstDomain = null;

		try {
			lstDomain = ApplicationConfig.getMapper().readValue(resource.getLstDomain(),
					TypeFactory.defaultInstance().constructCollectionType(List.class, targetClass));
			validator.validate(lstDomain);
		} catch (IOException e) {
			throw new JedaException(ErrorCode.TO_BE_DEFINE, String.format(
					"Resource [%s] to Domain impossible, fieldInfo doesn't match target %s", resource, targetClass), e);
		}

		return lstDomain;
	}

	private Validator	validator	= Validation.buildDefaultValidatorFactory().getValidator();

}
