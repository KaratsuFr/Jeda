package fr.ippon.tlse;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.ippon.tlse.business.GeneriqueBusiness;
import fr.ippon.tlse.dto.exception.GlobalRestExceptionMapper;
import fr.ippon.tlse.dto.exception.JedaRestExceptionMapper;
import fr.ippon.tlse.filter.PostRequestFilter;
import fr.ippon.tlse.filter.PreRequestFilter;
import fr.ippon.tlse.json.JacksonConfig;
import fr.ippon.tlse.rest.GeneriqueRestService;

public class ApplicationConfig extends Application {
	private Set<Object>				singletons	= new HashSet<>();
	private Set<Class<?>>			restClasses	= new HashSet<>();

	private static JacksonConfig	jacksonCon	= new JacksonConfig();

	public static ObjectMapper getMapper() {
		return jacksonCon.getMapper();
	}

	public ApplicationConfig() {
		singletons.add(new GeneriqueRestService());
		singletons.add(new PreRequestFilter());
		singletons.add(new PostRequestFilter());
		singletons.add(new GlobalRestExceptionMapper());
		singletons.add(new JedaRestExceptionMapper());
		singletons.add(jacksonCon);

		ApplicationUtils.SINGLETON.setGenBusiness(new GeneriqueBusiness<Object>());

		// register business Services
		// key: the domain class bean
		// value: instance object implements IBusinessService
		// ApplicationUtils.SINGLETON.registerNewBusinessService((DomainXXX.class, new BusinessXXX());

		// register persistence Services
		// key: name of the domain class bean
		// value: instance object implements IPersistenceManager
		// ApplicationUtils.SINGLETON.registerPersistentService((DomainXXX.class, new MongoPersistenceManager());
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
