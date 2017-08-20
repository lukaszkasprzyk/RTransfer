package com.rtransfer.internal.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.rtransfer.api.exception.ErrorCode;
import com.rtransfer.api.exception.ValidationException;

@Provider
public class ValidationExceptionProvider implements ExceptionMapper<ValidationException> {
	public Response toResponse(ValidationException exception) {
		Map<String, List<ErrorCode>> error = new HashMap<String, List<ErrorCode>>();
		error.put("errors", Arrays.asList(exception.getErrorCode()));
		Response response = Response.serverError().entity(error).build();
		return response;
	}
}
