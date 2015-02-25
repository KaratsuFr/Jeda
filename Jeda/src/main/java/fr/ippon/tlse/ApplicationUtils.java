package fr.ippon.tlse;

import java.io.IOException;
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

					private Map<String, IBusinessService>	mapBusinessService	= new HashMap<>();

					public void registerNewBusinessService(String domainClassSimpleName, IBusinessService service) {
						mapBusinessService.put(domainClassSimpleName, service);
					}

					private GeneriqueBusiness	genBusiness	= new GeneriqueBusiness();

					public void resetCacheClass() {
						cacheClassByName.invalidateAll();
					}

					public IBusinessService getBusinessServiceForClass(String domainClassSimpleName) {
						IBusinessService service = mapBusinessService.get(domainClassSimpleName);
						if (service == null) {
							service = genBusiness;
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
