package fr.ippon.tlse.persistence;

import java.util.Date;

import lombok.extern.slf4j.Slf4j;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.fakemongo.Fongo;
import com.mongodb.DB;

import fr.ippon.tlse.ApplicationContextUtils;
import fr.ippon.tlse.ApplicationUtils;
import fr.ippon.tlse.domain.DomainWithAllType;
import fr.ippon.tlse.domain.TuBasicDomain;
import fr.ippon.tlse.domain.sub1.TuBasicSub1Domain;

@Slf4j
@SuppressWarnings({ "rawtypes", "unchecked" })
public class MongoManagerTest {

	@BeforeClass
	public static void initDb() throws Exception {

		DB db = new Fongo("Test").getDB("Database");
		MongoPersistenceManager<Object> mongoP = new MongoPersistenceManager<Object>();
		mongoP.setDatabase(db);
		mongoP.setDatabaseName("Database");
		ApplicationUtils.SINGLETON.setDefaultPersistenceManager(mongoP);

		persistentManager = (MongoPersistenceManager<?>) ApplicationUtils.SINGLETON
				.getPersistenceServiceForClass(TuBasicDomain.class);
	}

	private static MongoPersistenceManager	persistentManager;

	@Test
	public void readOneBasicDomain() {
		int id = 22;
		TuBasicDomain bean = new TuBasicDomain("test", id);
		persistentManager.saveOrUpdate(bean);
		TuBasicDomain data = (TuBasicDomain) persistentManager.readOne(id, TuBasicDomain.class);
		Assert.assertNotNull(data);
		Assert.assertEquals(data, bean);
	}

	@Test
	public void readOneDomainWithAllType() {
		String id = "fqsdsd";
		DomainWithAllType bean = new DomainWithAllType();
		bean.setText(id);
		bean.setBool(true);
		bean.setOneDate(new Date());

		persistentManager.saveOrUpdate(bean);
		DomainWithAllType data = (DomainWithAllType) persistentManager.readOne("" + id, DomainWithAllType.class);
		Assert.assertNotNull(data);
		Assert.assertEquals(data, bean);
	}

	@Test
	public void readOneWithObjectId() {
		TuBasicSub1Domain bean = new TuBasicSub1Domain();
		bean.setUnTexte("dfjmkqfkq jmlk");
		persistentManager.saveOrUpdate(bean);
		log.debug("objectId {}", bean.get_id());
		TuBasicSub1Domain data = (TuBasicSub1Domain) persistentManager.readOne(bean.get_id(), TuBasicSub1Domain.class);
		Assert.assertNotNull(data);
		Assert.assertEquals(data.getUnTexte(), bean.getUnTexte());
	}

	@Test
	public void saveOrUpdate() {
		int id = 23;
		TuBasicDomain bean = new TuBasicDomain("test", id);
		persistentManager.saveOrUpdate(bean);
		TuBasicDomain bean2 = new TuBasicDomain("test2", id);

		persistentManager.saveOrUpdate(bean2);

		TuBasicDomain data = (TuBasicDomain) persistentManager.readOne(id, TuBasicDomain.class);
		Assert.assertNotNull(data);
		Assert.assertNotEquals(data, bean);
		Assert.assertEquals(data, bean2);

	}

	@Test(enabled = false)
	public void search() {

		throw new RuntimeException("Test not implemented");

	}

	@Test
	public void searchFromContextCriteria() {
		int id = 24;
		String textToFind = "toto";
		TuBasicDomain bean = new TuBasicDomain(textToFind, id);
		persistentManager.saveOrUpdate(bean);
		ApplicationContextUtils.SINGLETON.getQueryParam().add("text", textToFind);
		CursoWrapper<TuBasicDomain> data = persistentManager.searchFromContextCriteria(TuBasicDomain.class);
		Assert.assertNotNull(data);
		Assert.assertEquals(data.count(), 1);
		Assert.assertEquals(data.next(), bean);

	}
}
