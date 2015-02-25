package fr.ippon.tlse.dto;

import lombok.Data;

@Data
public class ValueDto {
	private Object	value;

	private String	errorCode;

	// for FK , related infos
	private String	urlResourceMapping;
}
