package fr.ippon.tlse.domain;

import lombok.Data;
import fr.ippon.tlse.annotation.Description;
import fr.ippon.tlse.annotation.Id;

/**
 * Dictonnary Domain are abstract class to have special domain value : key / value <br/>
 * Usefull for selectbox, constant.... <br/>
 *
 * @author mpages
 *
 */
@Data
public abstract class DictionnaryDomain {

	@Id
	@Description("Key of dictionnary to get corresponding key")
	private String	key;

	@Description("Value of dictionnary for corresponding key")
	private String	value;

	@Description("Long description of dictionnary Key value")
	String			description;
}
