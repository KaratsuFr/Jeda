package fr.ippon.tlse.business;

import java.util.List;

import org.apache.commons.lang3.NotImplementedException;

import fr.ippon.tlse.ApplicationUtils;
import fr.ippon.tlse.dto.ResourceDto;
import fr.ippon.tlse.persistence.CursoWrapper;
import fr.ippon.tlse.persistence.IPersistenceManager;

public class GeneriqueBusiness<T> implements IBusinessService<T> {

	@Override
	public CursoWrapper<T> readAll(Class<T> domainClass) {
		IPersistenceManager<T> dao = ApplicationUtils.SINGLETON.getPersistenceServiceForClass(domainClass);
		CursoWrapper<T> cursor = dao.searchFromContextCriteria(domainClass);
		return cursor;
		// List<T> lstDomainObj = new ArrayList<>();
		// while (cursor.hasNext()) {
		// lstDomainObj.add(cursor.next());
		// }
		// ResourceDto r = Domain2ResourceMapper.SINGLETON.buildResourceFromDomain(lstDomainObj, domainClass);
		// r.setTotalNbResult(cursor.count());
		// return r;
	}

	@Override
	public CursoWrapper<T> searchByCriteria(ResourceDto resource, Class<T> domainClass) {
		// TODO call DAO
		throw new NotImplementedException("searchByCriteria");
	}

	@Override
	public T readById(String id, Class<T> domainClass) {
		IPersistenceManager<T> dao = ApplicationUtils.SINGLETON.getPersistenceServiceForClass(domainClass);
		T bean = dao.readOne(id, domainClass);
		return bean;

		// List<T> lstDomainObj = new ArrayList<>();
		// lstDomainObj.add(bean);
		// return Domain2ResourceMapper.SINGLETON.buildResourceFromDomain(lstDomainObj, domainClass);
	}

	@Override
	public List<T> createOrUpdate(List<T> lstDomainObj, Class<T> domainClass) {
		// List<T> lstDomainObj = Resource2DomainMapper.SINGLETON.buildLstDomainFromResource(resource, domainClass);

		IPersistenceManager<T> dao = ApplicationUtils.SINGLETON.getPersistenceServiceForClass(domainClass);
		for (T object : lstDomainObj) {
			dao.saveOrUpdate(object);
		}
		return lstDomainObj;

		// return Domain2ResourceMapper.SINGLETON.buildResourceFromDomain(lstDomainObj, domainClass);

	}

	@Override
	public boolean deleteById(String id, Class<T> domainClass) {
		// TODO call DAO
		throw new NotImplementedException("deleteById");
	}

}
