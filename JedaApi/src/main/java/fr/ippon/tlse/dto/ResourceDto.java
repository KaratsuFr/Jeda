package fr.ippon.tlse.dto;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.JsonNode;

@Data
public class ResourceDto {

	@NotNull
	private String			label;

	private String			description;

	private int				positionOfId	= -1;

	private int				totalNbResult;

	@NotNull
	private String			className;

	private List<FieldDto>	lstFieldInfo	= new ArrayList<>();

	private Object			lstDomain;

	@JsonRawValue
	public String getLstDomain() {
		// default raw value: null or "[]"
		return lstDomain == null ? null : lstDomain.toString();
	}

	public void setLstDomain(JsonNode node) {
		lstDomain = node;
	}

	private List<String>	lstErrorCodes	= new ArrayList<>();
}
