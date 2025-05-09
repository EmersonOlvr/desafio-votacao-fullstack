package com.emerson.desafiovotacao.exception.http;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class BadRequestException extends HttpRuntimeException {

	private static final long serialVersionUID = 3984893269015753276L;

	public BadRequestException(String message) {
		super(message, HttpStatus.BAD_REQUEST);
	}

}
