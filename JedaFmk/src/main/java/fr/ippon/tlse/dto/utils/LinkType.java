package fr.ippon.tlse.dto.utils;

import lombok.Getter;

public enum LinkType {
	EDIT("", "/%s/edit?id=%s"), LIST(".list", "/%s/search");

	@Getter
	private String	relSuffix;

	@Getter
	private String	uriPattern;

	/**
	 * @param relSuffix
	 * @param uriPattern
	 */
	private LinkType(String relSuffix, String uriPattern) {
		this.relSuffix = relSuffix;
		this.uriPattern = uriPattern;
	}

}
