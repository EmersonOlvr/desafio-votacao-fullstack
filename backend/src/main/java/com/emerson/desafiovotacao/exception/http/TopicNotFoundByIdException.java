package com.emerson.desafiovotacao.exception.http;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class TopicNotFoundByIdException extends HttpRuntimeException {

	private static final long serialVersionUID = -6306015605351757275L;

	public TopicNotFoundByIdException() {
		super("Não existe nenhuma sessão de votação em aberto com o ID informado.", HttpStatus.NOT_FOUND);
	}

}
