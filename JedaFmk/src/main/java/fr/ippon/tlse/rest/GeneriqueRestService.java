package fr.ippon.tlse.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath.ClassInfo;

import fr.ippon.tlse.ApplicationContextUtils;
import fr.ippon.tlse.ApplicationUtils;
import fr.ippon.tlse.annotation.Domain;
import fr.ippon.tlse.business.IBusinessService;
import fr.ippon.tlse.dto.ResourceDto;
import fr.ippon.tlse.dto.utils.Domain2ResourceMapper;
import fr.ippon.tlse.dto.utils.Resource2DomainMapper;
import fr.ippon.tlse.persistence.CursoWrapper;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class GeneriqueRestService {

	@GET
	@Path("/app")
	public Response getListDomain(@QueryParam("parentNode") @DefaultValue("") String parentNode) {
		StringBuilder str = new StringBuilder(ApplicationUtils.SINGLETON.getDomainPackage());

		if (!StringUtils.isBlank(parentNode)) {
			str.append(".").append(parentNode);
		}
		log.debug("Looking for class in package {}", str);
		List<String> lstStringName = new ArrayList<>();
		ImmutableSet<ClassInfo> immutableSetClass = ApplicationUtils.SINGLETON.getClassForPackage(str.toString());
		for (ClassInfo classInfo : immutableSetClass) {
			if (classInfo.load().getAnnotation(Domain.class) != null) {
				lstStringName.add(classInfo.getSimpleName());
			}
		}

		return Response.ok(lstStringName).build();
	}

	// service to list all or one using param URL: id or parentId
	@SuppressWarnings("unchecked")
	@GET
	@Path("/entity/{entity:.*}")
	public <T> Response getAnyEntity(@PathParam("entity") String anyEntity) throws InstantiationException,
			IllegalAccessException {

		String hierachicalClassName = anyEntity.replace("/", ".");
		Class<T> targetDomainClass = null;
		try {
			targetDomainClass = (Class<T>) ApplicationUtils.SINGLETON.findDomainClassByName(hierachicalClassName);
		} catch (ExecutionException e) {
			return Response.status(Status.METHOD_NOT_ALLOWED)
					.entity(String.format("{\"message\": \"No Domain bean match %s \"}", anyEntity)).build();
		}

		ResourceDto result = null;
		MultivaluedMap<String, String> parameters = ApplicationContextUtils.SINGLETON.getQueryParam();
		// Special case to build new resource from empty object to provide create view
		if (parameters.containsKey(StandardUrlParameters.create.name())) {
			List<T> listDmainOneItemEmpty = new ArrayList<>();
			T defaultDomainBean = targetDomainClass.newInstance();
			// TODO call init method
			listDmainOneItemEmpty.add(defaultDomainBean);
			result = Domain2ResourceMapper.SINGLETON.buildResourceFromDomain(listDmainOneItemEmpty, targetDomainClass,
					false);
		} else {
			List<String> idParam = parameters.get(StandardUrlParameters.id.name());
			IBusinessService<T> service = ApplicationUtils.SINGLETON.getBusinessServiceForClass(targetDomainClass);

			if (idParam != null && idParam.size() == 1) {

				T bean = service.readById(idParam.get(0), targetDomainClass);
				if (bean != null) {
					List<T> lstDomainObj = Arrays.asList(bean);
					result = Domain2ResourceMapper.SINGLETON.buildResourceFromDomain(lstDomainObj, targetDomainClass,
							true);

				}
				if (result == null || result.getTotalNbResult() == 0) {
					return Response.status(Status.NO_CONTENT).entity(result).build();
				}

			} else {
				// List<String> parentIdParam = parameters.get(StandardUrlParameters.parentId.name());
				// TODO
				CursoWrapper<T> cursor = service.readAll(targetDomainClass);
				List<T> lstDomainObj = new ArrayList<>();
				while (cursor.hasNext()) {
					lstDomainObj.add(cursor.next());
				}
				result = Domain2ResourceMapper.SINGLETON
						.buildResourceFromDomain(lstDomainObj, targetDomainClass, false);
				result.setTotalNbResult(cursor.count());
			}
		}

		if (result == null) {
			return Response.status(Status.NO_CONTENT).entity(result).build();
		} else {
			return Response.ok(result).build();
		}
	}

	@SuppressWarnings("unchecked")
	@POST
	@Path("/entity/{entity:.*}")
	public <T> Response createOrUpdateAnyEntity(@PathParam("entity") String anyEntity, ResourceDto resourceToUp) {
		String hierachicalClassName = anyEntity.replace("/", ".");
		Class<T> targetDomainClass = null;
		try {
			targetDomainClass = (Class<T>) ApplicationUtils.SINGLETON.findDomainClassByName(hierachicalClassName);
		} catch (ExecutionException e) {
			return Response.status(Status.METHOD_NOT_ALLOWED)
					.entity(String.format("{\"message\": \"No Domain bean match %s \"}", anyEntity)).build();
		}

		IBusinessService<T> service = ApplicationUtils.SINGLETON.getBusinessServiceForClass(targetDomainClass);
		List<T> lstDomainObj = Resource2DomainMapper.SINGLETON.buildLstDomainFromResource(resourceToUp,
				targetDomainClass);
		List<T> lstDomainObjPersisted = service.createOrUpdate(lstDomainObj, targetDomainClass);
		ResourceDto resourcePersisted = Domain2ResourceMapper.SINGLETON.buildResourceFromDomain(lstDomainObjPersisted,
				targetDomainClass, true);
		return Response.ok().entity(resourcePersisted).build();
	}
}
