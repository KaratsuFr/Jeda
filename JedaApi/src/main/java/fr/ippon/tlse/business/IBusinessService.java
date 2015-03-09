package fr.ippon.tlse.business;

import java.util.List;

import fr.ippon.tlse.dto.ResourceDto;
import fr.ippon.tlse.persistence.CursoWrapper;

public interface IBusinessService<T> {

	CursoWrapper<T> readAll(Class<T> domainClass);

	CursoWrapper<T> searchByCriteria(ResourceDto resource, Class<T> domainClass);

	T readById(String id, Class<T> domainClass);

	List<T> createOrUpdate(List<T> lstDomain, Class<T> domainClass);

	boolean deleteById(String id, Class<T> domainClass);

}
