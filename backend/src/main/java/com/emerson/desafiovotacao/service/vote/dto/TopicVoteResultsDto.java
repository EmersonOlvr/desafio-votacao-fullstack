package com.emerson.desafiovotacao.service.vote.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TopicVoteResultsDto(
		UUID uuid, 
		String title, 
		String description, 
		Instant createdAt, 
		List<TopicVotingSessionVotesDto> votingSessionsVotes,
		long favorableVotes,
		long againstVotes,
		Result currentResult,
		Result finalResult,
		String currentTesultText,
		String finalResultText
) {

}
