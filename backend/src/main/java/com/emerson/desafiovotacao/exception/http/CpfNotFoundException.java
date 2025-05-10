package com.emerson.desafiovotacao.exception.http;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class CpfNotFoundException extends HttpRuntimeException {

	private static final long serialVersionUID = -2196990432220276986L;

	public CpfNotFoundException() {
		super("Informe um CPF válido.", HttpStatus.NOT_FOUND);
	}

}
