package fr.ippon.tlse;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.core.MultivaluedMap;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import org.jboss.resteasy.specimpl.MultivaluedMapImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import fr.ippon.tlse.business.GeneriqueBusiness;
import fr.ippon.tlse.business.IBusinessService;
import fr.ippon.tlse.dto.exception.ErrorCode;
import fr.ippon.tlse.dto.exception.JedaException;
import fr.ippon.tlse.persistence.IPersistenceManager;
import fr.ippon.tlse.persistence.MongoPersistenceManager;

public enum ApplicationUtils {
	SINGLETON;

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

	private final ThreadLocal<MultivaluedMap<String, String>>	currContext	= new ThreadLocal<MultivaluedMap<String, String>>()
																			{
																				@Override
																				protected MultivaluedMap<String, String> initialValue() {
																					return new MultivaluedMapImpl<String, String>();
																				}
																			};

	public MultivaluedMap<String, String> getQueryParam() {
		return currContext.get();
	}

	@Getter
	private String			basePackage;

	@Getter
	private String			domainPackage;

	@Getter
	@Setter
	private String			customDomainPackage;

	@Getter
	private ClassPath		classP;

	@Getter
	private ObjectMapper	mapper	= new ObjectMapper();

	private ApplicationUtils() {
		mapper.findAndRegisterModules();
		mapper.registerModule(new Jdk8Module());
		mapper.registerModule(new JSR310Module());

		basePackage = this.getClass().getPackage().getName();
		domainPackage = basePackage + ".domain";
		try {
			classP = ClassPath.from(this.getClass().getClassLoader());
		} catch (IOException e) {
			throw new JedaException(ErrorCode.TO_BE_DEFINE, "Unable to init application", e);

		}
	}

	public ImmutableSet<ClassInfo> getClassForPackage(String packageName) {
		return classP.getTopLevelClasses(packageName);
	}

	@SuppressWarnings("rawtypes")
	private Map<String, IBusinessService>		mapBusinessService		= new HashMap<>();
	@SuppressWarnings("rawtypes")
	private Map<String, IPersistenceManager>	mapPersistenceManager	= new HashMap<>();

	public <T> void registerNewBusinessService(Class<T> domainClass, IBusinessService<T> service) {
		mapBusinessService.put(domainClass.getName(), service);
	}

	public <T> void registerPersistentService(Class<T> domainClass, IPersistenceManager<T> service) throws Exception {
		service.configure();
		mapPersistenceManager.put(domainClass.getName(), service);
	}

	@SuppressWarnings("rawtypes")
	private GeneriqueBusiness	genBusiness	= new GeneriqueBusiness();
	@SuppressWarnings("rawtypes")
	private IPersistenceManager	defaultManager;

	public void resetCacheClass() {
		cacheClassByName.invalidateAll();
	}

	@SuppressWarnings("unchecked")
	public <T> IBusinessService<T> getBusinessServiceForClass(Class<T> domainClass) {
		IBusinessService<T> service = mapBusinessService.get(domainClass.getName());
		if (service == null) {
			service = genBusiness;
		}
		return service;
	}

	@SuppressWarnings("unchecked")
	public <T> IPersistenceManager<T> getPersistenceServiceForClass(Class<T> domainClass) {
		IPersistenceManager<T> service = mapPersistenceManager.get(domainClass.getName());
		if (service == null) {
			if (defaultManager == null) {
				// LAZY INIT manager at first call
				synchronized (this) {
					if (defaultManager == null) {
						try {
							defaultManager = new MongoPersistenceManager<T>().configure();
						} catch (UnknownHostException e) {
							throw new JedaException(ErrorCode.TO_BE_DEFINE,
									"Unable to init default persistent manager.", e);

						}
					}
				}
			}
			service = defaultManager;
		}
		return service;
	}

	@Getter(value = AccessLevel.PRIVATE)
	private LoadingCache<String, Class<?>>	cacheClassByName	= CacheBuilder.newBuilder().maximumSize(1000)
																		.build(new CacheLoader<String, Class<?>>()
																		{
																			@Override
																			public Class<?> load(
																					String hierachicalClassName)
																					throws ClassNotFoundException {
																				return getClassByName(hierachicalClassName);
																			}
																		});

	public Class<?> findDomainClassByName(String hierachicalClassName) throws ExecutionException {
		return getCacheClassByName().get(hierachicalClassName);
	}

	private Class<?> getClassByName(String hierachicalClassName) throws ClassNotFoundException {
		Class<?> targetDomainClass = null;
		try {
			targetDomainClass = Class.forName(String.format("%s.%s",
					ApplicationUtils.SINGLETON.getCustomDomainPackage(), hierachicalClassName));
		} catch (ClassNotFoundException e) {
			targetDomainClass = Class.forName(String.format("%s.%s", ApplicationUtils.SINGLETON.getDomainPackage(),
					hierachicalClassName));

		}
		return targetDomainClass;
	}
}
