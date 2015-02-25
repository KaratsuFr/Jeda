package fr.ippon.tlse;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import lombok.extern.slf4j.Slf4j;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.cache.LoadingCache;

import fr.ippon.tlse.domain.TuBasicDomain;
import fr.ippon.tlse.dto.FieldDto;
import fr.ippon.tlse.dto.utils.DtoMapper;

@Slf4j
public class MicroBenchTest {

	private final static int			NB_IT		= 20000;
	private final static int			NB_THREAD	= 4;

	private static Map<String, Long>	mapTimer	= new HashMap<>();
	private long						start;

	private static List<FieldDto>		lstFieldRef;

	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void initJdd() {
		lstFieldRef = ((List<FieldDto>) TestUtils.SINGLETON.invokePrivateMethode("buildFieldInfo", DtoMapper.SINGLETON,
				TuBasicDomain.class));
	}

	@BeforeMethod
	public void beforeMethod() {
		start = System.nanoTime();
	}

	@AfterMethod
	public void afterMethod(Method method) {
		long stop = System.nanoTime();
		mapTimer.put(method.getName(), stop - start);
	}

	@AfterClass
	public static void afterClass() {
		log.info("Summary MicroBench");
		for (Map.Entry<String, Long> entryTime : mapTimer.entrySet()) {
			log.info("{} - take {} ms", entryTime.getKey(), entryTime.getValue() / 1000_000);
			Long withCacheTime = mapTimer.get(entryTime.getKey() + "WithCache");
			if (withCacheTime != null) {
				log.info("Cache for method {} improved time by {}% and time is divide by {}", entryTime.getKey(),
						(Double.valueOf((entryTime.getValue() - withCacheTime)) * 100) / entryTime.getValue(),
						entryTime.getValue() / withCacheTime);
			}

		}

	}

	@SuppressWarnings("unchecked")
	@Test(threadPoolSize = NB_THREAD)
	public void testBuildFieldDto() {
		List<FieldDto> lstFieldDto = null;
		for (int i = 0; i < NB_IT; i++) {
			lstFieldDto = ((List<FieldDto>) TestUtils.SINGLETON.invokePrivateMethode("buildFieldInfo",
					DtoMapper.SINGLETON, TuBasicDomain.class));
			Assert.assertNotNull(lstFieldDto);
		}
		Assert.assertEquals(lstFieldDto, lstFieldRef);
	}

	@Test(threadPoolSize = NB_THREAD)
	public void testBuildFieldDtoWithCache() {
		@SuppressWarnings("unchecked")
		LoadingCache<Class<?>, List<FieldDto>> cache = (LoadingCache<Class<?>, List<FieldDto>>) TestUtils.SINGLETON
				.invokePrivateMethode("getCacheClassToFieldDto", DtoMapper.SINGLETON);
		List<FieldDto> lstFieldDto = null;
		for (int i = 0; i < NB_IT; i++) {
			lstFieldDto = cache.getUnchecked(TuBasicDomain.class);
			Assert.assertNotNull(lstFieldDto);
		}
		Assert.assertEquals(lstFieldDto, lstFieldRef);
	}

	@Test(threadPoolSize = NB_THREAD)
	public void testClassByName() {
		Class<?> classForName = null;
		for (int i = 0; i < NB_IT; i++) {
			classForName = (Class<?>) TestUtils.SINGLETON.invokePrivateMethode("getClassByName",
					ApplicationUtils.SINGLETON, TuBasicDomain.class.getSimpleName());
			Assert.assertNotNull(classForName);
		}
		Assert.assertEquals(classForName.getName(), TuBasicDomain.class.getName());
	}

	@Test(threadPoolSize = NB_THREAD)
	public void testClassByNameWithCache() throws ExecutionException {
		@SuppressWarnings("unchecked")
		LoadingCache<String, Class<?>> cache = (LoadingCache<String, Class<?>>) TestUtils.SINGLETON
				.invokePrivateMethode("getCacheClassByName", ApplicationUtils.SINGLETON);
		Class<?> classForName = null;

		for (int i = 0; i < NB_IT; i++) {
			classForName = cache.get(TuBasicDomain.class.getSimpleName());
			Assert.assertNotNull(classForName);
		}
		Assert.assertEquals(classForName.getName(), TuBasicDomain.class.getName());
	}
}
