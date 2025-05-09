package com.emerson.desafiovotacao.exception.http;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class ConflictException extends HttpRuntimeException {

	private static final long serialVersionUID = -7742033817392207878L;

	public ConflictException(String message) {
		super(message, HttpStatus.CONFLICT);
	}

}
