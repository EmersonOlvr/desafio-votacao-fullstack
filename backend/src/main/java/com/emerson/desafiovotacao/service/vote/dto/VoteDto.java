package com.emerson.desafiovotacao.service.vote.dto;

import java.time.Instant;

public record VoteDto(
		String cpf, 
		boolean vote, 
		Instant votedAt
) {

}
