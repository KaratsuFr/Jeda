package fr.ippon.tlse.domain;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.validator.constraints.NotBlank;

import fr.ippon.tlse.annotation.Description;
import fr.ippon.tlse.annotation.Domain;

@Data
@Description("basic domain bean for test")
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Domain(label = "BasicBean", idColumnName = "num")
public class TuBasicDomain {

	@Size(max = 100)
	@NotBlank
	@Description("simple text field")
	private String	text;

	@Max(1000)
	@Min(0)
	private Integer	num	= null;

}
