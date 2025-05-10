package com.emerson.desafiovotacao.service.vote;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

import com.emerson.desafiovotacao.domain.topic.Topic;
import com.emerson.desafiovotacao.domain.topic.TopicVotingSession;
import com.emerson.desafiovotacao.domain.vote.Vote;
import com.emerson.desafiovotacao.exception.http.ConflictException;
import com.emerson.desafiovotacao.exception.http.TopicVotingSessionNotFoundByIdException;
import com.emerson.desafiovotacao.exception.http.TopicVotingSessionNotFoundByTopicException;
import com.emerson.desafiovotacao.external.CpfValidationClient;
import com.emerson.desafiovotacao.external.CpfValidationResponse;
import com.emerson.desafiovotacao.external.VoteEligibilityStatus;
import com.emerson.desafiovotacao.repository.topic.TopicVotingSessionRepository;
import com.emerson.desafiovotacao.repository.vote.VoteRepository;
import com.emerson.desafiovotacao.service.topic.TopicService;
import com.emerson.desafiovotacao.service.topic.TopicVotingSessionService;
import com.emerson.desafiovotacao.service.topic.dto.TopicDto;

import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
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

	@TestConfiguration
	static class MockCpfValidationClientConfig {

		@Bean
		@Primary
		CpfValidationClient cpfValidationClient() {
			// retorna um mock do cliente
			CpfValidationClient mock = Mockito.mock(CpfValidationClient.class);
			
			// define o comportamento padrão para qualquer CPF
			Mockito.when(mock.validateCpf(Mockito.anyString()))
				   .thenReturn(new CpfValidationResponse(VoteEligibilityStatus.ABLE_TO_VOTE));
			
			return mock;
		}
	}

	@Test
	@DisplayName("Deve registrar um voto com sucesso usando o ID da pauta")
	void shouldVoteSuccessfullyByTopicUuid() {
		Topic topic = this.topicService.create(new TopicDto("Pauta Votação", "Descrição da pauta"));
		this.votingSessionService.startVotingSession(topic.getUuid(), 5);

		String cpf = "16643151015";
		this.voteService.voteByTopicUuid(topic.getUuid(), cpf, true);

		Optional<Vote> savedVote = this.voteRepository.findByTopicVotingSessionTopicUuidAndCpf(topic.getUuid(), cpf);
		assertTrue(savedVote.isPresent());
		assertEquals(true, savedVote.get().getVote());
	}

	@Test
	@DisplayName("Deve lançar exceção se a pauta não possui sessão ativa")
	void shouldThrowIfTopicHasNoActiveSession() {
		Topic topic = this.topicService.create(new TopicDto("Pauta sem sessão", "Descrição da pauta"));

		String cpf = "96889461096";

		TopicVotingSessionNotFoundByTopicException exception = assertThrows(TopicVotingSessionNotFoundByTopicException.class, () -> {
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
		String cpf = "28682801027";
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

		String cpf = "55252488088";
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

		String cpf = "50882863096";
		
		TopicVotingSessionNotFoundByIdException exception = assertThrows(TopicVotingSessionNotFoundByIdException.class, () -> {
			this.voteService.voteByVotingSessionUuid(session.getUuid(), cpf, true);
		});
		assertEquals("Não foi possível encontrar pauta ativa com o ID informado.", exception.getMessage());
	}

}
