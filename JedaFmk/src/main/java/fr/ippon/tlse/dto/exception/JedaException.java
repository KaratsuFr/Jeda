package fr.ippon.tlse.dto.exception;

import lombok.Getter;

public class JedaException extends RuntimeException {

	/**
	 *
	 */
	private static final long	serialVersionUID	= -4726066725559742762L;

	@Getter
	private ErrorCode			errorCode;

	/**
	 *
	 */
	public JedaException(ErrorCode errorCode) {
		super();
		this.errorCode = errorCode;
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public JedaException(ErrorCode errorCode, String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.errorCode = errorCode;
	}

	/**
	 * @param message
	 * @param cause
	 */
	public JedaException(ErrorCode errorCode, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	/**
	 * @param message
	 */
	public JedaException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	/**
	 * @param cause
	 */
	public JedaException(ErrorCode errorCode, Throwable cause) {
		super(cause);
		this.errorCode = errorCode;
	}

}
