package com.emerson.desafiovotacao.service.vote.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TopicVotingSessionVotesDto(
		UUID uuid, 
		Instant startTime, 
		Instant endTime, 
		TopicVotingSessionStatus status,
		List<VoteDto> votes
) {

}
