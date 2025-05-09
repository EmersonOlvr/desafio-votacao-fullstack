package com.emerson.desafiovotacao.exception.http;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HttpRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -3855223399151104729L;
	
	private HttpStatus httpStatus;
	
	public HttpRuntimeException(String message, HttpStatus httpStatus) {
		super(message);
		this.httpStatus = httpStatus;
	}
	
}
