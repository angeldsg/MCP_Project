package com.mcp.demo.exception;

import java.io.Serializable;

import org.springframework.http.HttpStatus;

public class RestException extends Exception implements Serializable {

	private static final long serialVersionUID = 1L;
	private HttpStatus status;
	private String message;

	
	public RestException(HttpStatus statusCode, final String arguments) {
		super(arguments);
		status = statusCode;
		message = arguments;
	}

	public RestException(final String arguments, final Throwable cause) {
		super(arguments, cause);
		message = arguments;
	}


	@Override
	public final String getMessage() {
		return message;
	}

	public HttpStatus getStatus() {
		return status;
	}


}
