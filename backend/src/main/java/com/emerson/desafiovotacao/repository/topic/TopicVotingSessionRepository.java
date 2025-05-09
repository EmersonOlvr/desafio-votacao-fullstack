package com.emerson.desafiovotacao.repository.topic;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.emerson.desafiovotacao.domain.topic.TopicVotingSession;

public interface TopicVotingSessionRepository extends JpaRepository<TopicVotingSession, UUID> {

	boolean existsByTopicUuidAndEndTimeGreaterThan(UUID topicUuid, Instant endTime);

	Optional<TopicVotingSession> findTopByTopicUuidAndEndTimeGreaterThanOrderByStartTimeDesc(UUID topicUuid, Instant endTime);
	
	Optional<TopicVotingSession> findByUuidAndEndTimeGreaterThan(UUID id, Instant endTime);

	List<TopicVotingSession> findByTopicUuid(UUID topicUuid);

}
