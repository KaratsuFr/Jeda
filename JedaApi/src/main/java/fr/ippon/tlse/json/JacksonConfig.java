package fr.ippon.tlse.json;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import lombok.Getter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

@Provider
public class JacksonConfig implements ContextResolver<ObjectMapper> {

	@Getter
	private final ObjectMapper	mapper	= new ObjectMapper();

	public JacksonConfig() {
		mapper.findAndRegisterModules();
		mapper.registerModule(new Jdk8Module());
		mapper.registerModule(new JSR310Module());
	}

	@Override
	public ObjectMapper getContext(Class<?> arg0) {
		return mapper;
	}

}
