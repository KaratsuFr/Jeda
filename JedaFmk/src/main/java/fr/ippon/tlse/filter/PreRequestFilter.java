package fr.ippon.tlse.filter;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import fr.ippon.tlse.ApplicationUtils;

@Provider
public class PreRequestFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		ApplicationUtils.SINGLETON.getQueryParam().putAll(requestContext.getUriInfo().getQueryParameters());
		ApplicationUtils.SINGLETON.setCurrRestPath(requestContext.getUriInfo().getPath().toString());

	}

}
