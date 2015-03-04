package fr.ippon.tlse.dto;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ResourceDto {

	@NotNull
	private String					label;

	private String					description;

	private int						positionOfId	= -1;

	private int						totalNbResult;

	@NotNull
	private String					className;

	private List<FieldDto>			lstFieldInfo	= new ArrayList<>();

	private List<List<ValueDto>>	lstValues		= new ArrayList<>();

	private List<String>			lstErrorCodes	= new ArrayList<>();
}
