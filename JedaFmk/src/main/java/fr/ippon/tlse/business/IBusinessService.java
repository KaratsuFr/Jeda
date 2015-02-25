package fr.ippon.tlse.business;

import java.util.Optional;

import fr.ippon.tlse.dto.ResourceDto;

public interface IBusinessService {

	ResourceDto readAll(Class<?> domainClass, Optional<String> parentId);

	ResourceDto searchByCriteria(ResourceDto resource, Class<?> domainClass);

	ResourceDto readById(String id, Class<?> domainClass);

	ResourceDto createOrUpdate(ResourceDto resource, Class<?> domainClass);

	boolean deleteById(String id, Class<?> domainClass);

}
