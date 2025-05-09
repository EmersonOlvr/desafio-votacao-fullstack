package com.emerson.desafiovotacao.exception.http;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class NotFoundException extends HttpRuntimeException {

	private static final long serialVersionUID = -363472785455941083L;
	
	private static String RESOURCE_NOT_FOUND_MESSAGE = "Não foi possível encontrar %s com %s: %s";
	
	public NotFoundException(String label) {
		super(String.format("Não foi possível encontrar este %s", label), HttpStatus.NOT_FOUND);
	}

	public NotFoundException(String label, String id) {
		
		this(label, "o ID", id);
	}

	public NotFoundException(String label, String field, String id) {
		
		super(String.format(RESOURCE_NOT_FOUND_MESSAGE, label, field, id), HttpStatus.NOT_FOUND);
	}

}
