package com.emerson.desafiovotacao.service.vote;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.emerson.desafiovotacao.domain.topic.Topic;
import com.emerson.desafiovotacao.domain.topic.TopicVotingSession;
import com.emerson.desafiovotacao.domain.vote.Vote;
import com.emerson.desafiovotacao.exception.http.ConflictException;
import com.emerson.desafiovotacao.exception.http.NotFoundTopicVotingSessionByIdException;
import com.emerson.desafiovotacao.exception.http.NotFoundTopicVotingSessionByTopicException;
import com.emerson.desafiovotacao.repository.topic.TopicVotingSessionRepository;
import com.emerson.desafiovotacao.repository.vote.VoteRepository;
import com.emerson.desafiovotacao.service.topic.TopicService;
import com.emerson.desafiovotacao.service.topic.TopicVotingSessionService;
import com.emerson.desafiovotacao.service.topic.dto.TopicDto;

import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class VoteServiceTest {

	@Autowired
	private VoteService voteService;
	
	@Autowired
	private TopicService topicService;
	
	@Autowired
	private TopicVotingSessionService votingSessionService;

	@Autowired
	private VoteRepository voteRepository;
	
	@Autowired
	private TopicVotingSessionRepository votingSessionRepository;

	@Test
	@DisplayName("Deve registrar um voto com sucesso usando o ID da pauta")
	void shouldVoteSuccessfullyByTopicUuid() {
		Topic topic = this.topicService.create(new TopicDto("Pauta Votação", "Descrição da pauta"));
		this.votingSessionService.startVotingSession(topic.getUuid(), 5);

		String cpf = "12345678900";
		this.voteService.voteByTopicUuid(topic.getUuid(), cpf, true);

		Optional<Vote> savedVote = this.voteRepository.findByTopicVotingSessionTopicUuidAndCpf(topic.getUuid(), cpf);
		assertTrue(savedVote.isPresent());
		assertEquals(true, savedVote.get().getVote());
	}

	@Test
	@DisplayName("Deve lançar exceção se a pauta não possui sessão ativa")
	void shouldThrowIfTopicHasNoActiveSession() {
		Topic topic = this.topicService.create(new TopicDto("Pauta sem sessão", "Descrição da pauta"));

		String cpf = "12345678901";

		NotFoundTopicVotingSessionByTopicException exception = assertThrows(NotFoundTopicVotingSessionByTopicException.class, () -> {
			this.voteService.voteByTopicUuid(topic.getUuid(), cpf, false);
		});
		assertEquals("Não existe nenhuma sessão de votação em aberto para o tópico informado.", exception.getMessage());
	}

	@Test
	@DisplayName("Deve lançar exceção se o associado já votou na pauta")
	void shouldThrowIfAssociateAlreadyVoted() {
		Topic topic = this.topicService.create(new TopicDto("Pauta com voto duplicado", "Descrição da pauta"));
		TopicVotingSession session = this.votingSessionService.startVotingSession(topic.getUuid(), 5);

		// vota a primeira vez
		String cpf = "12345678902";
		this.voteService.voteByVotingSessionUuid(session.getUuid(), cpf, false);

		// certifica-se que não deixará votar novamente
		ConflictException exception = assertThrows(ConflictException.class, () -> this.voteService.voteByVotingSessionUuid(session.getUuid(), cpf, false));
		assertEquals("O associado já votou nesta pauta. Só é permitido votar uma vez por pauta.", exception.getMessage());
	}

	@Test
	@DisplayName("Deve registrar um voto com sucesso usando o ID da sessão")
	void shouldVoteSuccessfullyBySessionUuid() {
		Topic topic = this.topicService.create(new TopicDto("Outra Pauta", "Descrição da pauta"));
		TopicVotingSession session = this.votingSessionService.startVotingSession(topic.getUuid(), 5);

		String cpf = "12345678903";
		this.voteService.voteByVotingSessionUuid(session.getUuid(), cpf, false);

		Optional<Vote> savedVote = this.voteRepository.findByTopicVotingSessionTopicUuidAndCpf(topic.getUuid(), cpf);
		assertTrue(savedVote.isPresent());
		assertEquals(false, savedVote.get().getVote());
	}

	@Test
	@DisplayName("Deve lançar exceção se a sessão informada não estiver ativa")
	void shouldThrowIfSessionNotActive() {
		Topic topic = this.topicService.create(new TopicDto("Pauta Inativa", "Descrição da pauta"));
		
		// cria uma sessão já finalizada (data anterior à atual)
		TopicVotingSession session = this.votingSessionRepository.save(
				TopicVotingSession.builder()
						.topic(topic)
						.startTime(Instant.now().minus(10, ChronoUnit.MINUTES))
						.endTime(Instant.now().minus(5, ChronoUnit.MINUTES))
						.build()
		);

		String cpf = "12345678904";
		
		NotFoundTopicVotingSessionByIdException exception = assertThrows(NotFoundTopicVotingSessionByIdException.class, () -> {
			this.voteService.voteByVotingSessionUuid(session.getUuid(), cpf, true);
		});
		assertEquals("Não foi possível encontrar pauta ativa com o ID informado.", exception.getMessage());
	}

}
