package fr.ippon.tlse.persistence;

import java.util.Map;

public interface IPersistenceManager<T> {

	IPersistenceManager<T> configure() throws Exception;

	CursoWrapper<T> search(Map<String, Object> criteria, Class<T> type);

	CursoWrapper<T> searchFromContextCriteria(Class<T> type);

	T readOne(Object id, Class<T> type);

	void saveOrUpdate(T objectToPersist);

}
