package fr.ippon.tlse.persistence;

import java.net.UnknownHostException;
import java.util.Map;

import lombok.NoArgsConstructor;
import lombok.Setter;

import org.apache.commons.lang3.NotImplementedException;
import org.bson.types.ObjectId;
import org.jongo.Find;
import org.jongo.FindOne;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

import fr.ippon.tlse.ApplicationContextUtils;

@NoArgsConstructor
public class MongoPersistenceManager<T> implements IPersistenceManager<T> {

	private Jongo			jongo;

	@Setter
	private static DB		database;

	@Setter
	private static String	host			= "localhost";

	@Setter
	private static int		port			= 27017;

	@Setter
	private static String	databaseName	= "jedadb";

	@Override
	public IPersistenceManager<T> configure() throws UnknownHostException {
		if (database == null) {
			database = new MongoClient(host, port).getDB(databaseName);
		}
		jongo = new Jongo(database);
		return this;
	}

	@Override
	public void saveOrUpdate(T objectToPersist) {
		innerSaveOrUpdate(objectToPersist);
	}

	public WriteResult innerSaveOrUpdate(T objectToPersist) {
		MongoCollection collOfT = jongo.getCollection(objectToPersist.getClass().getSimpleName());
		return collOfT.save(objectToPersist);
	}

	@Override
	public T readOne(Object id, Class<T> type) {
		MongoCollection collOfT = jongo.getCollection(type.getSimpleName());
		FindOne item = null;
		if (ObjectId.class.isAssignableFrom(id.getClass())) {
			item = collOfT.findOne((ObjectId) id);
		} else if (Number.class.isAssignableFrom(id.getClass())) {
			item = collOfT.findOne(String.format("{_id: %s}", id));
		} else {
			item = collOfT.findOne(String.format("{_id: \"%s\"}", id));
		}
		return item.as(type);
	}

	@Override
	public CursoWrapper<T> searchFromContextCriteria(Class<T> type) {
		MongoCollection collOfT = jongo.getCollection(type.getSimpleName());

		Map<String, String> criteria = ApplicationContextUtils.SINGLETON.getParameters();
		StringBuilder str = new StringBuilder("{");
		boolean first = true;
		for (Map.Entry<String, String> entryValue : criteria.entrySet()) {
			if (!first) {
				first = false;
				str.append(",");
			}
			str.append(entryValue.getKey()).append(": \"").append(entryValue.getValue()).append("\"");
		}
		str.append("}");
		Find r = collOfT.find(str.toString());

		r.limit(ApplicationContextUtils.SINGLETON.getLimit());
		return new MongoCursoWrapper<T>(r.as(type));
	}

	// FIXME todo full search
	@Override
	public CursoWrapper<T> search(Map<String, Object> criteria, Class<T> type) {
		throw new NotImplementedException("Mongo search");
	}
}
