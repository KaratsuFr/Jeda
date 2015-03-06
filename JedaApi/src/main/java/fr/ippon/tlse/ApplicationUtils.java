package fr.ippon.tlse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import fr.ippon.tlse.business.IBusinessService;
import fr.ippon.tlse.dto.exception.ErrorCode;
import fr.ippon.tlse.dto.exception.JedaException;
import fr.ippon.tlse.persistence.IPersistenceManager;

public enum ApplicationUtils {
	SINGLETON;

	@Getter
	private String		basePackage;

	@Getter
	private String		domainPackage;

	@Getter
	@Setter
	private String		customDomainPackage;

	@Getter
	private ClassPath	classP;

	private ApplicationUtils() {
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
	@Setter
	private IBusinessService	genBusiness;

	@SuppressWarnings("rawtypes")
	private IPersistenceManager	defaultManager;

	public void setDefaultPersistenceManager(IPersistenceManager<?> per) throws Exception {
		defaultManager = per;
		defaultManager.configure();
	}

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
