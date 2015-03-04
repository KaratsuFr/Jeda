package fr.ippon.tlse.domain.sub1;

import lombok.Data;

import org.bson.types.ObjectId;

import fr.ippon.tlse.annotation.Domain;

@Domain(label = "Domain2")
@Data
public class TuBasicSub1Domain {

	private ObjectId	_id;

	private String		unTexte;
}
