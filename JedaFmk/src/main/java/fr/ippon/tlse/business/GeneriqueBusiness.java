package fr.ippon.tlse.business;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;

import fr.ippon.tlse.ApplicationUtils;
import fr.ippon.tlse.dto.ResourceDto;
import fr.ippon.tlse.dto.utils.Domain2ResourceMapper;
import fr.ippon.tlse.dto.utils.Resource2DomainMapper;
import fr.ippon.tlse.persistence.CursoWrapper;
import fr.ippon.tlse.persistence.IPersistenceManager;

public class GeneriqueBusiness<T> implements IBusinessService<T> {

	@Override
	public ResourceDto readAll(Class<T> domainClass) {
		IPersistenceManager<T> dao = ApplicationUtils.SINGLETON.getPersistenceServiceForClass(domainClass);
		CursoWrapper<T> cursor = dao.searchFromContextCriteria(domainClass);

		List<T> lstDomainObj = new ArrayList<>();
		while (cursor.hasNext()) {
			lstDomainObj.add(cursor.next());
		}
		ResourceDto r = Domain2ResourceMapper.SINGLETON.buildResourceFromDomain(lstDomainObj, domainClass);
		r.setTotalNbResult(cursor.count());
		return r;
	}

	@Override
	public ResourceDto searchByCriteria(ResourceDto resource, Class<T> domainClass) {
		// TODO call DAO
		throw new NotImplementedException("searchByCriteria");
	}

	@Override
	public ResourceDto readById(String id, Class<T> domainClass) {
		IPersistenceManager<T> dao = ApplicationUtils.SINGLETON.getPersistenceServiceForClass(domainClass);
		T bean = dao.readOne(id, domainClass);

		List<T> lstDomainObj = new ArrayList<>();
		lstDomainObj.add(bean);
		return Domain2ResourceMapper.SINGLETON.buildResourceFromDomain(lstDomainObj, domainClass);
	}

	@Override
	public ResourceDto createOrUpdate(ResourceDto resource, Class<T> domainClass) {
		List<T> lstDomainObj = Resource2DomainMapper.SINGLETON.buildLstDomainFromResource(resource, domainClass);

		IPersistenceManager<T> dao = ApplicationUtils.SINGLETON.getPersistenceServiceForClass(domainClass);
		for (T object : lstDomainObj) {
			dao.saveOrUpdate(object);
		}

		return Domain2ResourceMapper.SINGLETON.buildResourceFromDomain(lstDomainObj, domainClass);

	}

	@Override
	public boolean deleteById(String id, Class<T> domainClass) {
		// TODO call DAO
		throw new NotImplementedException("deleteById");
	}

}
