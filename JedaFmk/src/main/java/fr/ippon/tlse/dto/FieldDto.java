package fr.ippon.tlse.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class FieldDto {
	private int					order				= -1;
	private String				groupName			= "default";
	private int					groupOrder			= -1;
	private boolean				displayCrud			= true;
	private boolean				displaySearch		= true;
	private boolean				displayList			= true;

	private String				label;
	private String				description;
	private String				javaType;
	private String				jsType;

	private String				fieldName;
	private boolean				searchInvert		= false;
	private boolean				searchIgnoreCase	= false;

	private boolean				isId				= false;
	private String				urlTypeaheadService;

	private List<KeyValueDto>	lstConstraint		= new ArrayList<>();
}
