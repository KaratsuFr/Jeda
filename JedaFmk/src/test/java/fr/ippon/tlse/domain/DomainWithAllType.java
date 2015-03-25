package fr.ippon.tlse.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import lombok.Data;
import fr.ippon.tlse.annotation.Domain;
import fr.ippon.tlse.annotation.Id;

@Data
@Domain(label = "superModel")
public class DomainWithAllType implements DomainBean {

	@Id
	private String						text;

	private Character					oneChar;

	private char						primitiveChar;

	private Boolean						bool;

	private boolean						primitiveBool;

	private Byte						oneByte;

	private byte						primitiveByte;

	private Short						oneShort;
	private short						primitiveShort;

	private Integer						oneInt;

	private int							primitiveInt;

	private Long						oneLong;
	private long						primitiveLong;

	private Float						oneFloat;

	private float						primitiveFloat;

	private Double						oneDouble;

	private double						primitiveDouble;

	// supported with limit .. Domain can t contains inner @Embended
	// @Embended
	private Collection<TuBasicDomain>	collDomain;

	// NotSupported
	// Map<String, TuBasicDomain> mapDomain;

	private Optional<String>			optionnalObj;

	// @Embended
	private TuBasicDomain				domain;

	private Date						oneDate;

	// Supported but test failed due to invalid generic equals on calendar
	// private Calendar oneCalendar;

	private LocalDate					localDate;

	private LocalDateTime				localDateTime;

}
