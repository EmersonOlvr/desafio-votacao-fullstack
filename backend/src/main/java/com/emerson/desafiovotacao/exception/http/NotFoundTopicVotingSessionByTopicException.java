package com.emerson.desafiovotacao.exception.http;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class NotFoundTopicVotingSessionByTopicException extends HttpRuntimeException {

	private static final long serialVersionUID = 7779579427541077129L;

	public NotFoundTopicVotingSessionByTopicException() {
		super("Não existe nenhuma sessão de votação em aberto para o tópico informado.", HttpStatus.NOT_FOUND);
	}

}
