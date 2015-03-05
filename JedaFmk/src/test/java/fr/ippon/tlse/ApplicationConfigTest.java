package fr.ippon.tlse;

import com.github.fakemongo.Fongo;
import com.mongodb.DB;

import fr.ippon.tlse.domain.TuBasicDomain;
import fr.ippon.tlse.persistence.MongoPersistenceManager;

public class ApplicationConfigTest extends ApplicationConfig {

	public ApplicationConfigTest() {
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
		MongoPersistenceManager.setDatabase(db);
		MongoPersistenceManager.setDatabaseName("Database");
	}

}
