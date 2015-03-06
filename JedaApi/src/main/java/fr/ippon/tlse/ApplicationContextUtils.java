package fr.ippon.tlse;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import lombok.Setter;
import fr.ippon.tlse.rest.StandardUrlParameters;

public enum ApplicationContextUtils {

	SINGLETON;

	@Setter
	private static int											defaultLimit	= 100;

	private final ThreadLocal<MultivaluedMap<String, String>>	currContext		= new ThreadLocal<MultivaluedMap<String, String>>()
																				{
																					@Override
																					protected MultivaluedMap<String, String> initialValue() {
																						return new MultivaluedHashMap<String, String>();
																					}
																				};

	public MultivaluedMap<String, String> getQueryParam() {
		return currContext.get();
	}

	public int getLimit() {
		List<String> ctxtLimit = getQueryParam().get("limit");
		if (ctxtLimit == null || ctxtLimit.isEmpty()) {
			return defaultLimit;
		} else {
			return Integer.valueOf(ctxtLimit.get(0));
		}
	}

	public Map<String, String> getParameters() {
		Set<Entry<String, List<String>>> entryParams = getQueryParam().entrySet();

		return entryParams.stream().filter(entry -> !StandardUrlParameters.validParam(entry.getKey()))
				.collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue().get(0)));
	}

	private final ThreadLocal<String>	currPath	= new ThreadLocal<String>()
													{
														@Override
														protected String initialValue() {
															return "";
														}
													};

	public String getCurrRestPath() {
		return currPath.get();
	}

	public void setCurrRestPath(String path) {
		currPath.set(path);
	}
}
