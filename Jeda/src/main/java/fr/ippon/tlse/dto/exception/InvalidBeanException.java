package fr.ippon.tlse.dto.exception;

import fr.ippon.tlse.ApplicationUtils;

public class InvalidBeanException extends JedaException {

	/**
	 *
	 */
	private static final long	serialVersionUID	= -3603478359601615795L;

	public InvalidBeanException(Class<?> beanClass) {
		super(ErrorCode.TO_BE_DEFINE, String.format("%s is not a Domain bean - bean should be in package %s or %s",
				beanClass.getName(), ApplicationUtils.SINGLETON.getDomainPackage(),
				ApplicationUtils.SINGLETON.getCustomDomainPackage()));
	}
}
