package com.emerson.desafiovotacao.service.vote;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.emerson.desafiovotacao.domain.topic.Topic;
import com.emerson.desafiovotacao.domain.topic.TopicVotingSession;
import com.emerson.desafiovotacao.domain.vote.Vote;
import com.emerson.desafiovotacao.repository.topic.TopicVotingSessionRepository;
import com.emerson.desafiovotacao.repository.vote.VoteRepository;
import com.emerson.desafiovotacao.service.topic.TopicService;
import com.emerson.desafiovotacao.service.topic.TopicVotingSessionService;
import com.emerson.desafiovotacao.service.topic.dto.TopicDto;
import com.emerson.desafiovotacao.service.vote.dto.Result;
import com.emerson.desafiovotacao.service.vote.dto.TopicVoteResultsDto;

import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class VoteResultsServiceTest {

	@Autowired
	private VoteResultsService voteResultsService;
	
	@Autowired
	private TopicService topicService;
	
	@Autowired
	private TopicVotingSessionService votingSessionService;

	@Autowired
	private TopicVotingSessionRepository votingSessionRepository;

	@Autowired
	private VoteRepository voteRepository;

	@Test
	@DisplayName("Deve retornar resultado com votos FAVORÁVEIS sendo maioria")
	void shouldReturnFavorableMajorityResults() {
		Topic topic = this.topicService.create(new TopicDto("Pauta Favorável", "Descrição da pauta"));
		TopicVotingSession session = this.votingSessionService.startVotingSession(topic.getUuid(), 5);

		this.voteRepository.saveAll(List.of(
			new Vote(session, "11100000000", true, Instant.now()),
			new Vote(session, "22200000000", true, Instant.now()),
			new Vote(session, "33300000000", false, Instant.now())
		));

		TopicVoteResultsDto result = this.voteResultsService.getResultsByTopicUuid(topic.getUuid());

		assertEquals(2, result.favorableVotes());
		assertEquals(1, result.againstVotes());
		assertEquals(Result.FAVORABLE, result.currentResult());
		assertNull(result.finalResult());
	}

	@Test
	@DisplayName("Deve retornar resultado com votos empatados")
	void shouldReturnTiedResult() {
		Topic topic = this.topicService.create(new TopicDto("Pauta Empatada", "Descrição da pauta"));
		TopicVotingSession session = this.votingSessionService.startVotingSession(topic.getUuid(), 5);

		this.voteRepository.saveAll(List.of(
			new Vote(session, "11100000000", true, Instant.now()),
			new Vote(session, "22200000000", false, Instant.now())
		));

		TopicVoteResultsDto result = this.voteResultsService.getResultsByTopicUuid(topic.getUuid());

		assertEquals(Result.TIED, result.currentResult());
		assertEquals("Votos EMPATADOS até o momento", result.currentResultText());
		assertNull(result.finalResult());
	}

	@Test
	@DisplayName("Deve retornar resultado FINAL se sessões já acabaram e com votos CONTRA sendo maioria")
	void shouldReturnFinalResultIfAllSessionsClosedAndAgainstMajorityResults() {
		Topic topic = this.topicService.create(new TopicDto("Pauta Fechada", "Descrição da pauta"));

		TopicVotingSession session = this.votingSessionRepository.save(
			TopicVotingSession.builder()
				.topic(topic)
				.startTime(Instant.now().minus(30, ChronoUnit.MINUTES))
				.endTime(Instant.now().minus(1, ChronoUnit.MINUTES))
				.build()
		);

		this.voteRepository.saveAll(List.of(
			new Vote(session, "11100000000", false, Instant.now()),
			new Vote(session, "22200000000", false, Instant.now())
		));

		TopicVoteResultsDto result = this.voteResultsService.getResultsByTopicUuid(topic.getUuid());

		assertEquals(0, result.favorableVotes());
		assertEquals(2, result.againstVotes());
		assertEquals(Result.AGAINST, result.currentResult());
		assertEquals(Result.AGAINST, result.finalResult());
		assertEquals("Maioria dos votos CONTRA", result.finalResultText());
	}

	@Test
	@DisplayName("Deve retornar mensagem de nenhum voto")
	void shouldReturnNoVotesMessage() {
		Topic topic = this.topicService.create(new TopicDto("Pauta Sem Voto", "Descrição da pauta"));
		this.votingSessionService.startVotingSession(topic.getUuid(), 5);

		TopicVoteResultsDto result = this.voteResultsService.getResultsByTopicUuid(topic.getUuid());

		assertEquals(0, result.favorableVotes());
		assertEquals(0, result.againstVotes());
		assertEquals("Nenhum voto até o momento", result.currentResultText());
		assertNull(result.finalResult());
	}

	@Test
	@DisplayName("Deve retornar mensagem de sessões em andamento")
	void shouldReturnMessageIfSessionsStillOpen() {
		Topic topic = this.topicService.create(new TopicDto("Pauta Sessão Aberta", "Descrição da pauta"));
		this.votingSessionService.startVotingSession(topic.getUuid(), 5);

		TopicVoteResultsDto result = this.voteResultsService.getResultsByTopicUuid(topic.getUuid());

		assertTrue(result.finalResultText().contains("em andamento"));
		assertNull(result.finalResult());
	}

}
