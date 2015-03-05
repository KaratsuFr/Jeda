package fr.ippon.tlse;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Setter;
import fr.ippon.tlse.rest.StandardUrlParameters;

public enum ApplicationContextUtils {

	SINGLETON;

	@Setter
	private static int	defaultLimit	= 100;

	public int getLimit() {
		List<String> ctxtLimit = ApplicationUtils.SINGLETON.getQueryParam().get("limit");
		if (ctxtLimit == null || ctxtLimit.isEmpty()) {
			return defaultLimit;
		} else {
			return Integer.valueOf(ctxtLimit.get(0));
		}
	}

	public Map<String, String> getParameters() {
		Set<Entry<String, List<String>>> entryParams = ApplicationUtils.SINGLETON.getQueryParam().entrySet();

		return entryParams.stream().filter(entry -> !StandardUrlParameters.validParam(entry.getKey()))
				.collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue().get(0)));
	}
}
