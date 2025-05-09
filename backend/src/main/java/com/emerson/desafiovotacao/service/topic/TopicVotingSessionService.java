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

@Service
public class TopicVotingSessionService {
	
	@Autowired
	private TopicVotingSessionRepository repository;
	
	@Autowired
	private TopicService topicService;

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
