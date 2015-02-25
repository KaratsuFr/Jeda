package fr.ippon.tlse.business;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.NotImplementedException;

import fr.ippon.tlse.dto.ResourceDto;
import fr.ippon.tlse.dto.utils.DtoMapper;

public class GeneriqueBusiness implements IBusinessService {

	@Override
	public ResourceDto readAll(Class<?> domainClass, Optional<String> parentId) {
		// TODO call DAO
		throw new NotImplementedException("readAll");
	}

	@Override
	public ResourceDto searchByCriteria(ResourceDto resource, Class<?> domainClass) {
		// TODO call DAO
		throw new NotImplementedException("searchByCriteria");
	}

	@Override
	public ResourceDto readById(String id, Class<?> domainClass) {
		// TODO call DAO
		throw new NotImplementedException("readById");
	}

	@Override
	public ResourceDto createOrUpdate(ResourceDto resource, Class<?> domainClass) {
		List<?> lstDomainObj = DtoMapper.SINGLETON.buildLstDomainFromResource(resource, domainClass);
		// TODO call DAO

		return DtoMapper.SINGLETON.buildResourceFromDomain(lstDomainObj);

	}

	@Override
	public boolean deleteById(String id, Class<?> domainClass) {
		// TODO call DAO
		throw new NotImplementedException("deleteById");
	}

}
