package com.emerson.desafiovotacao.service.topic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.emerson.desafiovotacao.domain.topic.Topic;
import com.emerson.desafiovotacao.domain.topic.TopicVotingSession;
import com.emerson.desafiovotacao.exception.http.ConflictException;
import com.emerson.desafiovotacao.repository.topic.TopicRepository;
import com.emerson.desafiovotacao.service.topic.dto.TopicDto;

import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TopicVotingSessionServiceTest {
	
	@Autowired
	private TopicService topicService;

	@Autowired
	private TopicVotingSessionService votingSessionService;

	@Autowired
	private TopicRepository topicRepository;

	@Test
	@DisplayName("Deve iniciar uma sessão de votação com sucesso")
	void shouldStartVotingSessionSuccessfully() {
		Topic topic = this.topicService.create(new TopicDto("Pauta X", "Descrição da pauta"));

		TopicVotingSession session = this.votingSessionService.startVotingSession(topic.getUuid(), 5);
		Duration duration = Duration.between(session.getStartTime(), session.getEndTime());
		
		assertNotNull(session.getUuid());
		assertEquals(topic.getUuid(), session.getTopic().getUuid());
		assertTrue(session.getEndTime().isAfter(session.getStartTime()));
		assertEquals(5, duration.toMinutes());
	}

	@Test
	@DisplayName("Deve lançar uma exceção se já existir sessão ativa para a pauta")
	void shouldThrowConflictIfVotingSessionAlreadyExists() {
		Topic topic = this.topicRepository.save(new Topic("Pauta duplicada", "Descrição", Instant.now()));
		
		// iniciar uma sessão de votação
		this.votingSessionService.startVotingSession(topic.getUuid(), 5);

		// deve lançar uma exception porque tentou iniciar uma nova sessão de votação,
		// sendo que já existe uma sessão de votação em andamento (criada logo acima)
		ConflictException exception = assertThrows(ConflictException.class, () -> {
			this.votingSessionService.startVotingSession(topic.getUuid(), 5);
		});

		assertEquals("Já existe uma sessão de votação em andamento para a pauta informada.", exception.getMessage());
	}

}
