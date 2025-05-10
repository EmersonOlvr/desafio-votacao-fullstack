package com.emerson.desafiovotacao.service.vote;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.emerson.desafiovotacao.domain.topic.Topic;
import com.emerson.desafiovotacao.domain.vote.Vote;
import com.emerson.desafiovotacao.repository.topic.TopicVotingSessionRepository;
import com.emerson.desafiovotacao.repository.vote.VoteRepository;
import com.emerson.desafiovotacao.service.topic.TopicService;
import com.emerson.desafiovotacao.service.vote.dto.Result;
import com.emerson.desafiovotacao.service.vote.dto.TopicVoteResultsDto;
import com.emerson.desafiovotacao.service.vote.dto.TopicVotingSessionStatus;
import com.emerson.desafiovotacao.service.vote.dto.TopicVotingSessionVotesDto;
import com.emerson.desafiovotacao.service.vote.dto.VoteDto;

import lombok.extern.slf4j.Slf4j;

/**
 * Serviço responsável por calcular e fornecer os resultados de votos das sessões de votação de uma pauta.
 * Inclui métodos para retornar os resultados de uma pauta específica, considerando todas as sessões de votação associadas.
 * 
 * @author Emerson Oliveira
 */
@Service
@Slf4j
public class VoteResultsService {
	
	@Autowired
	private TopicVotingSessionRepository votingSessionRepository;
	
	@Autowired
	private VoteRepository voteRepository;
	
	@Autowired
	private TopicService topicService;
	
	/**
	 * Obtém os resultados de votos de uma pauta específica identificada pelo UUID.
	 * 
	 * Este método calcula o total de votos favoráveis e contrários em todas as sessões de votação associadas à pauta.
	 * Ele também gera o texto do resultado atual e final, levando em consideração se há sessões de votação em andamento.
	 * 
	 * @param topicUuid O identificador único da pauta para a qual os resultados de votação serão retornados.
	 * @return Um DTO contendo os resultados de votação da pauta.
	 */
	public TopicVoteResultsDto getResultsByTopicUuid(UUID topicUuid) {
		Topic topic = this.topicService.get(topicUuid);
		
		log.info(String.format("Obtendo votos da pauta \"%s\"...", topic.getTitle()));
		List<Vote> votesDb = this.voteRepository.findByTopicVotingSessionTopicUuid(topicUuid);
		log.info(String.format("%s votos obtidos da pauta \"%s\".", votesDb.size(), topic.getTitle()));
		
		Instant now = Instant.now();
		List<TopicVotingSessionVotesDto> votingSessionsVotes = this.votingSessionRepository.findByTopicUuid(topicUuid)
				.stream()
				.map(s -> {
					List<VoteDto> votingSessionVotes = votesDb.stream()
														.filter(v -> v.getTopicVotingSession().getUuid().equals(s.getUuid()))
														.map(v -> new VoteDto(
																v.getCpf(), 
																v.getVote(), 
																v.getVotedAt()
														))
														.toList();
					
					return new TopicVotingSessionVotesDto(
						s.getUuid(),
						s.getStartTime(),
						s.getEndTime(),
						now.compareTo(s.getEndTime()) > 0 
							? TopicVotingSessionStatus.FINISHED 
							: TopicVotingSessionStatus.OPEN,
						votingSessionVotes
					);
				})
				.toList();
		
		long favorableVotes = 0;
		long againstVotes = 0;
		
		List<VoteDto> allVotes = votingSessionsVotes.stream().flatMap(s -> s.votes().stream()).toList();
		for (VoteDto v : allVotes) {
			if (v.vote())
				favorableVotes++;
			else
				againstVotes++;
		}
		
		boolean hasOpenedSessions = votingSessionsVotes.isEmpty()
				|| votingSessionsVotes.stream().anyMatch(s -> TopicVotingSessionStatus.OPEN.equals(s.status()));
		
		boolean hasVotes = favorableVotes > 0 || againstVotes > 0;
		Result currentResult = hasVotes 
				? (favorableVotes == againstVotes 
						? Result.TIED 
						: (favorableVotes > againstVotes 
								? Result.FAVORABLE 
								: Result.AGAINST
						)
				) 
				: null;
		Result finalResult = !hasOpenedSessions ? currentResult : null;
		
		String currentResultText = currentResult != null
				? (Result.TIED.equals(currentResult) 
						? String.format("Votos EMPATADOS %s", hasOpenedSessions ? "até o momento" : "").trim() 
						: (String.format(
								"Maioria dos votos %s %s", 
								Result.FAVORABLE.equals(currentResult)  ? "FAVORÁVEIS" : "CONTRA", 
								hasOpenedSessions ? "até o momento" : ""
							).trim()
						)
				)
				: "Nenhum voto até o momento";
		String finalResultText = !hasOpenedSessions ? currentResultText : "Ainda há sessões de votação em andamento";
		
		return new TopicVoteResultsDto(
			topic.getUuid(), 
			topic.getTitle(),
			topic.getDescription(),
			topic.getCreatedAt(),
			votingSessionsVotes,
			favorableVotes,
			againstVotes,
			currentResult,
			finalResult,
			currentResultText,
			finalResultText
		);
	}

}
