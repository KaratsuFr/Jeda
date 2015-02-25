package fr.ippon.tlse;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import fr.ippon.tlse.dto.exception.GlobalRestExceptionMapper;
import fr.ippon.tlse.dto.exception.JedaRestExceptionMapper;
import fr.ippon.tlse.filter.PostRequestFilter;
import fr.ippon.tlse.filter.PreRequestFilter;
import fr.ippon.tlse.rest.GeneriqueRestService;

public class ApplicationConfig extends Application {
	private Set<Object>		singletons	= new HashSet<>();
	private Set<Class<?>>	restClasses	= new HashSet<>();

	public ApplicationConfig() {
		singletons.add(new GeneriqueRestService());
		singletons.add(new PreRequestFilter());
		singletons.add(new PostRequestFilter());
		singletons.add(new GlobalRestExceptionMapper());
		singletons.add(new JedaRestExceptionMapper());
		// register business Services
		// key: name of the domain bean (without extension)
		// value: instance object implements IBusinessService
		// ApplicationUtils.SINGLETON.registerNewBusinessService(("DomainXXX", new BusinessXXX());
	}

	@Override
	public Set<Class<?>> getClasses() {
		return restClasses;
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}

}
