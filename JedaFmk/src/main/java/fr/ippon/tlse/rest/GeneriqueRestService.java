package fr.ippon.tlse.rest;

import java.util.ArrayList;
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

import fr.ippon.tlse.ApplicationUtils;
import fr.ippon.tlse.annotation.Domain;
import fr.ippon.tlse.business.IBusinessService;
import fr.ippon.tlse.dto.ResourceDto;
import fr.ippon.tlse.dto.utils.Domain2ResourceMapper;

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
	public <T> Response getAnyEntity(@PathParam("entity") String anyEntity) {

		String hierachicalClassName = anyEntity.replace("/", ".");
		Class<T> targetDomainClass = null;
		try {
			targetDomainClass = (Class<T>) ApplicationUtils.SINGLETON.findDomainClassByName(hierachicalClassName);
		} catch (ExecutionException e) {
			return Response.status(Status.METHOD_NOT_ALLOWED)
					.entity(String.format("{\"message\": \"No Domain bean match %s \"}", anyEntity)).build();
		}

		ResourceDto result = null;
		MultivaluedMap<String, String> parameters = ApplicationUtils.SINGLETON.getQueryParam();
		// Special case to build new resource from empty object to provide create view
		if (parameters.containsKey(StandardUrlParameters.create.name())) {
			List<T> listDmainOneItemEmpty = new ArrayList<>();
			result = Domain2ResourceMapper.SINGLETON.buildResourceFromDomain(listDmainOneItemEmpty, targetDomainClass);
		} else {
			List<String> idParam = parameters.get(StandardUrlParameters.id.name());
			IBusinessService<T> service = ApplicationUtils.SINGLETON.getBusinessServiceForClass(targetDomainClass);

			if (idParam != null && idParam.size() == 1) {
				result = service.readById(idParam.get(0), targetDomainClass);
			} else {
				// List<String> parentIdParam = parameters.get(StandardUrlParameters.parentId.name());
				// TODO
				result = service.readAll(targetDomainClass);
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
		ResourceDto resourcePersisted = service.createOrUpdate(resourceToUp, targetDomainClass);
		return Response.ok().entity(resourcePersisted).build();
	}
}
