package fr.ippon.tlse.dto.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {
	TO_BE_DEFINE("to_be_define");

	@Getter
	private String	errorCode;

}
