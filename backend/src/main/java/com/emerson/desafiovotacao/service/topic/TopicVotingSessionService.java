package com.emerson.desafiovotacao.service.topic;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.emerson.desafiovotacao.domain.topic.Topic;
import com.emerson.desafiovotacao.domain.topic.TopicVotingSession;
import com.emerson.desafiovotacao.exception.http.ConflictException;
import com.emerson.desafiovotacao.repository.topic.TopicVotingSessionRepository;

/**
 * Serviço responsável pela gestão das sessões de votação das pautas.
 * Contém métodos para iniciar uma nova sessão de votação para uma pauta específica.
 * 
 * @author Emerson Oliveira
 */
@Service
public class TopicVotingSessionService {
	
	@Autowired
	private TopicVotingSessionRepository repository;
	
	@Autowired
	private TopicService topicService;

	/**
	 * Inicia uma nova sessão de votação para a pauta especificada.
	 * 
	 * Verifica se já existe uma sessão de votação em andamento para a pauta informada.
	 * Se não houver, cria e persiste uma nova sessão de votação com o tempo de duração especificado.
	 * 
	 * @param topicUuid O identificador único da pauta para a qual a sessão de votação será iniciada.
	 * @param durationInMinutes A duração da sessão de votação em minutos.
	 * @return A sessão de votação criada e persistida no banco de dados.
	 * @throws ConflictException Caso já exista uma sessão de votação em andamento para a pauta informada.
	 */
	public TopicVotingSession startVotingSession(UUID topicUuid, int durationInMinutes) {
		Instant now = Instant.now();
		if (this.repository.existsByTopicUuidAndEndTimeGreaterThan(topicUuid, now))
			throw new ConflictException("Já existe uma sessão de votação em andamento para a pauta informada.");
		
		Topic topic = this.topicService.get(topicUuid);
		
		Instant endTime = now.plus(durationInMinutes, ChronoUnit.MINUTES);
		
		TopicVotingSession topicVotingSession = TopicVotingSession.builder()
																.startTime(now)
																.endTime(endTime)
																.topic(topic)
																.build();
		
		return this.repository.save(topicVotingSession);
	}
	
}
