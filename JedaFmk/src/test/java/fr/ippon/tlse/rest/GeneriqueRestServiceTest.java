package fr.ippon.tlse.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.spi.ResteasyUriInfo;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import fr.ippon.tlse.ApplicationContextUtils;
import fr.ippon.tlse.domain.TuBasicDomain;
import fr.ippon.tlse.domain.sub1.TuBasicSub1Domain;

@SuppressWarnings("unchecked")
public class GeneriqueRestServiceTest {
	private GeneriqueRestService	respService	= new GeneriqueRestService();

	@BeforeClass
	public void init() throws URISyntaxException {
		ApplicationContextUtils.SINGLETON.setUriInfo(new ResteasyUriInfo(new URI("http://localhost:9999/test")));
	}

	@Test
	public void getListDomainRoot() {
		Response response = respService.getListDomain(null);

		List<String> lstClass = (List<String>) response.getEntity();
		boolean basicDomainClassPresent = false;
		for (String className : lstClass) {
			if (StringUtils.equals(className, TuBasicDomain.class.getSimpleName())) {
				basicDomainClassPresent = true;
			}
		}

		Assert.assertTrue(basicDomainClassPresent, "Class " + TuBasicDomain.class.getSimpleName()
				+ " not found in Root package");
	}

	@Test
	public void getListDomainSub1() throws IOException {
		Response response = respService.getListDomain("sub1");
		List<String> lstClass = (List<String>) response.getEntity();
		boolean basicDomainClassPresent = false;
		for (String className : lstClass) {
			if (StringUtils.equals(className, TuBasicSub1Domain.class.getSimpleName())) {
				basicDomainClassPresent = true;
			}
			if (StringUtils.equals(className, TuBasicDomain.class.getSimpleName())) {
				Assert.fail(TuBasicDomain.class.getSimpleName() + " should not be accessible.");
			}
		}
		Assert.assertTrue(basicDomainClassPresent, "Class " + TuBasicSub1Domain.class.getSimpleName()
				+ " not found in Root package");
	}
}
