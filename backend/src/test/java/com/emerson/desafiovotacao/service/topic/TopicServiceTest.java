package com.emerson.desafiovotacao.service.topic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.emerson.desafiovotacao.domain.topic.Topic;
import com.emerson.desafiovotacao.exception.http.ConflictException;
import com.emerson.desafiovotacao.exception.http.TopicVotingSessionNotFoundByIdException;
import com.emerson.desafiovotacao.repository.topic.TopicRepository;
import com.emerson.desafiovotacao.service.topic.dto.TopicDto;

import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TopicServiceTest {

	@Autowired
	private TopicService service;

	@Autowired
	private TopicRepository repository;

	@Test
	@DisplayName("Deve retornar uma pauta quando o ID dela existe na base")
	void shouldReturnTopicWhenIdExists() {
		Topic topic = new Topic("Título único", "Descrição qualquer", Instant.now());
		topic = this.repository.save(topic);

		Topic result = this.service.get(topic.getUuid());

		assertEquals(topic.getUuid(), result.getUuid());
		assertEquals(topic.getTitle(), result.getTitle());
	}

	@Test
	@DisplayName("Deve lançar uma exceção quando o ID da pauta não existe na base")
	void shouldThrowNotFoundExceptionWhenIdDoesNotExist() {
		UUID id = UUID.randomUUID();

		TopicVotingSessionNotFoundByIdException exception = assertThrows(TopicVotingSessionNotFoundByIdException.class, () -> service.get(id));
		assertEquals("Não foi possível encontrar pauta ativa com o ID informado.", exception.getMessage());
	}

	@Test
	@DisplayName("Deve cadastrar uma pauta com sucesso quando o título dela é único")
	void shouldCreateTopicSuccessfullyWhenTitleIsUnique() {
		TopicDto dto = new TopicDto("Título único", "Descrição qualquer");

		Topic result = this.service.create(dto);

		// garante que criou
		assertNotNull(result.getUuid());
		assertEquals(dto.title(), result.getTitle());

		// garante que permaneceu na base
		Optional<Topic> fromDb = this.repository.findById(result.getUuid());
		assertTrue(fromDb.isPresent());
	}

	@Test
	@DisplayName("Deve lançar uma exceção quando tentar criar uma pauta com um título já existente")
	void shouldThrowConflictExceptionWhenTitleAlreadyExists() {
		Topic existing = new Topic("Título existente", "Descrição", Instant.now());
		this.repository.save(existing);

		TopicDto dto = new TopicDto("Título existente", "Outra descrição");

		ConflictException exception = assertThrows(ConflictException.class, () -> service.create(dto));
		assertEquals("Já existe uma pauta com o título informado.", exception.getMessage());
	}

}
