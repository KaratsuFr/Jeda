package fr.ippon.tlse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import com.github.fakemongo.Fongo;
import com.mongodb.DB;

import fr.ippon.tlse.domain.DomainWithAllType;
import fr.ippon.tlse.domain.TuBasicDomain;
import fr.ippon.tlse.persistence.IPersistenceManager;
import fr.ippon.tlse.persistence.MongoPersistenceManager;

public class ApplicationConfigTest extends ApplicationConfig {

	public ApplicationConfigTest() throws Exception {
		super();
		// register business Services
		// key: the domain class bean
		// value: instance object implements IBusinessService
		// ApplicationUtils.SINGLETON.registerNewBusinessService((DomainXXX.class, new BusinessXXX());

		// register persistence Services
		// key: name of the domain class bean
		// value: instance object implements IPersistenceManager
		// ApplicationUtils.SINGLETON.registerPersistentService((DomainXXX.class, new MongoPersistenceManager());
		ApplicationUtils.SINGLETON.registerNewBusinessService(TuBasicDomain.class, new MockTuBasicBuniess());

		DB db = new Fongo("Test").getDB("Database");
		MongoPersistenceManager<Object> mongoP = new MongoPersistenceManager<Object>();
		mongoP.setDatabase(db);
		mongoP.setDatabaseName("Database");
		ApplicationUtils.SINGLETON.setDefaultPersistenceManager(mongoP);

		generateFullDomOnMongo(mongoP);
	}

	private void generateFullDomOnMongo(IPersistenceManager<Object> pers) {
		DomainWithAllType fullDom = new DomainWithAllType();
		fullDom.setBool(Boolean.TRUE);
		// fullDom.setCollDomain(Arrays.asList(new TuBasicDomain[] { new TuBasicDomain("sds dfs fd", 1),
		// new TuBasicDomain("sds mm mmmfd", 2) }));

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
		fullDom.setDomain(new TuBasicDomain("sdsfd", 1));
		fullDom.setOneDate(new Date());
		// fullDom.setOneCalendar(Calendar.getInstance());
		fullDom.setLocalDate(LocalDate.now());
		fullDom.setLocalDateTime(LocalDateTime.now());

		pers.saveOrUpdate(fullDom);
	}

}
