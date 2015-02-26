package fr.ippon.tlse.dto.utils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import fr.ippon.tlse.ApplicationUtils;
import fr.ippon.tlse.domain.DomainWithAllType;
import fr.ippon.tlse.domain.TuBasicDomain;
import fr.ippon.tlse.dto.FieldDto;
import fr.ippon.tlse.dto.ResourceDto;
import fr.ippon.tlse.dto.ValueDto;

@Slf4j
public class DtoMapperTest {

	@DataProvider
	public Object[][] lstDomainValid() {

		List<TuBasicDomain> lstDomain = new ArrayList<>();
		{
			TuBasicDomain b = new TuBasicDomain();
			b.setText("fdqfdsq ");
			b.setNum(10);
			lstDomain.add(b);

			TuBasicDomain c = new TuBasicDomain();
			c.setText("t");
			c.setNum(11);

			lstDomain.add(c);

			TuBasicDomain d = new TuBasicDomain();
			d.setText("testv dfsqq fdsqfqds fdsqfdq");
			d.setNum(12);

			lstDomain.add(d);
		}

		List<TuBasicDomain> lstDomain2 = new ArrayList<>();
		TuBasicDomain a = new TuBasicDomain();
		a.setText("test");
		a.setNum(33);
		lstDomain2.add(a);

		List<DomainWithAllType> lstDomain3 = new ArrayList<>();
		DomainWithAllType fullDom = new DomainWithAllType();
		fullDom.setBool(Boolean.TRUE);
		// fullDom.setCollDomain(lstDomain);
		Map<String, TuBasicDomain> mapX = new HashMap<>();
		mapX.put("xx", a);
		// fullDom.setMapDomain(mapX);
		fullDom.setOneByte(Byte.MAX_VALUE);
		fullDom.setOneChar('h');
		fullDom.setOneDouble(234567d);
		fullDom.setOneFloat(09876.456f);
		fullDom.setOneInt(687);
		fullDom.setOneLong(Long.MAX_VALUE);
		fullDom.setOneShort((short) 1000);
		fullDom.setOptionnalObj(Optional.of("testXD"));
		fullDom.setPrimitiveBool(true);
		fullDom.setPrimitiveByte((byte) 55);
		fullDom.setPrimitiveChar('d');
		fullDom.setPrimitiveDouble(5765765.79878d);
		fullDom.setPrimitiveFloat(0.1f);
		fullDom.setPrimitiveInt(456);
		fullDom.setPrimitiveLong(345678l);
		fullDom.setPrimitiveShort((short) 77);
		fullDom.setText("hlkhjk");
		fullDom.setDomain(a);
		fullDom.setOneDate(new Date());
		// fullDom.setOneCalendar(Calendar.getInstance());
		fullDom.setLocalDate(LocalDate.now());
		fullDom.setLocalDateTime(LocalDateTime.now());
		lstDomain3.add(fullDom);

		return new Object[][] { new Object[] { lstDomain }, new Object[] { lstDomain2 }, new Object[] { lstDomain3 } };

	}

	@DataProvider
	public Object[][] lstDomainInvalid() {

		List<Object> lstDomain = new ArrayList<>();
		{

			TuBasicDomain c = new TuBasicDomain();
			c.setText("             ");
			c.setNum(-1);
			lstDomain.add(c);

			TuBasicDomain d = new TuBasicDomain();
			d.setText("dfqfsqd dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddtestv dfsqq fdsqfqds fdsqfdq");
			d.setNum(2000);
			lstDomain.add(d);
		}

		List<TuBasicDomain> lstDomain2 = new ArrayList<>();
		TuBasicDomain a = new TuBasicDomain();
		a.setText(null);
		a.setNum(1001);
		lstDomain2.add(a);

		return new Object[][] { new Object[] { lstDomain }, new Object[] { lstDomain2 } };

	}

	@Test(dataProvider = "lstDomainValid")
	public void testResource2DomainReverse(List<Object> lstDomain) throws IOException {
		ResourceDto result = DtoMapper.SINGLETON.buildResourceFromDomain(lstDomain);

		String jsonResourceDto = ApplicationUtils.SINGLETON.getMapper().writeValueAsString(result);
		ResourceDto r2 = ApplicationUtils.SINGLETON.getMapper().readValue(jsonResourceDto, result.getClass());

		List<?> lstDomainResul = DtoMapper.SINGLETON.buildLstDomainFromResource(r2, lstDomain.get(0).getClass());

		Assert.assertEquals(lstDomainResul.size(), lstDomain.size());
		for (int i = 0; i < lstDomainResul.size(); i++) {
			Assert.assertEquals(lstDomainResul.get(i), lstDomain.get(i));
		}
		log.debug("testResource2DomainReverse origine {} - build {}", lstDomain, lstDomainResul);

	}

	@Test(dataProvider = "lstDomainValid")
	public void buildResourceFromDomain(List<Object> lstDomain) {
		log.info("test buildResourceFromDomain with {}", lstDomain.toString());
		ResourceDto result = DtoMapper.SINGLETON.buildResourceFromDomain(lstDomain);
		Assert.assertNotNull(result);
		log.info(result.toString());
		Assert.assertNotNull(result.getLstFieldInfo());
		Assert.assertNotNull(result.getLstValues());
		Assert.assertEquals(result.getLstValues().size(), lstDomain.size());

		if (lstDomain.size() > 0) {
			for (List<ValueDto> lstValueDto : result.getLstValues()) {
				for (ValueDto valueDto : lstValueDto) {
					Assert.assertNotNull(valueDto.getValue());
					Assert.assertNull(valueDto.getErrorCode());
				}
			}

			Assert.assertNotNull(result.getClassName());

			boolean boolTextFieldFound = false;
			for (FieldDto fInfo : result.getLstFieldInfo()) {
				if (StringUtils.equals(fInfo.getFieldName(), "text")) {
					log.info(fInfo.toString());
					boolTextFieldFound = true;
				}

			}
			Assert.assertTrue(boolTextFieldFound, "no field dto found");
		}
	}

	@Test(dataProvider = "lstDomainInvalid")
	public void buildResourceFromDomainError(List<Object> lstDomain) {
		log.info("test buildResourceFromDomain with {}", lstDomain.toString());
		ResourceDto result = DtoMapper.SINGLETON.buildResourceFromDomain(lstDomain);
		Assert.assertNotNull(result);
		log.info(result.toString());
		Assert.assertNotNull(result.getLstFieldInfo());
		Assert.assertNotNull(result.getLstValues());
		Assert.assertEquals(result.getLstValues().size(), lstDomain.size());

		if (lstDomain.size() > 0) {
			for (List<ValueDto> lstValueDto : result.getLstValues()) {
				for (ValueDto valueDto : lstValueDto) {
					Assert.assertNotNull(valueDto.getErrorCode(), "ValueDto is" + valueDto.toString());
				}
			}

			Assert.assertNotNull(result.getClassName());

			boolean boolTextFieldFound = false;
			for (FieldDto fInfo : result.getLstFieldInfo()) {
				if (StringUtils.equals(fInfo.getFieldName(), "text")) {
					log.info(fInfo.toString());
					boolTextFieldFound = true;
				}

			}
			Assert.assertTrue(boolTextFieldFound, "no field dto found");
		}
	}
}
