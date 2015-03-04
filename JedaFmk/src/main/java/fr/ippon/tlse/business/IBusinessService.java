package fr.ippon.tlse.business;

import fr.ippon.tlse.dto.ResourceDto;

public interface IBusinessService<T> {

	ResourceDto readAll(Class<T> domainClass);

	ResourceDto searchByCriteria(ResourceDto resource, Class<T> domainClass);

	ResourceDto readById(String id, Class<T> domainClass);

	ResourceDto createOrUpdate(ResourceDto resource, Class<T> domainClass);

	boolean deleteById(String id, Class<T> domainClass);

}
