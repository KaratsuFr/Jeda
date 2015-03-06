package fr.ippon.tlse.rest;

import org.apache.commons.lang3.StringUtils;

public enum StandardUrlParameters {
	id, parentId, create, limit;

	public static boolean validParam(String param) {
		boolean valid = false;
		StandardUrlParameters[] tabParams = StandardUrlParameters.values();
		for (int i = 0; i < tabParams.length && valid == false; i++) {
			if (StringUtils.equalsIgnoreCase(param, tabParams[i].name())) {
				valid = true;
			}
		}
		return valid;
	}
}
