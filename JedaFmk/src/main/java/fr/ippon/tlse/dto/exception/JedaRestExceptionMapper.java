package fr.ippon.tlse.dto.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import lombok.extern.slf4j.Slf4j;
import fr.ippon.tlse.ApplicationContextUtils;

@Slf4j
@Provider
public class JedaRestExceptionMapper implements ExceptionMapper<JedaException> {

	@Override
	public Response toResponse(JedaException exception) {
		log.error("Service on URL {} and QueryParam: {}- has fail with error. See exception.",
				ApplicationContextUtils.SINGLETON.getCurrRestPath(), ApplicationContextUtils.SINGLETON.getQueryParam(),
				exception);
		return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON)
				.entity(String.format("\"errorCode\" : \"%s\"", exception.getErrorCode())).build();
	}

}
