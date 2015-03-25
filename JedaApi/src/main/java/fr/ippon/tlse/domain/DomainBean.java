package fr.ippon.tlse.domain;

import fr.ippon.tlse.dto.exception.ErrorCode;
import fr.ippon.tlse.dto.exception.JedaException;

public interface DomainBean {

	public default DomainBean init() {
		try {
			return getClass().newInstance();
		} catch (InstantiationException | IllegalAccessException ex) {
			throw new JedaException(ErrorCode.TO_BE_DEFINE, "Unable to create DomainBean with name:"
					+ getClass().getName(), ex);
		}
	}
}
