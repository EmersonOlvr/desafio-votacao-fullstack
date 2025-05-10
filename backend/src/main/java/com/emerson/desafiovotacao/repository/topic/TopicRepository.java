package com.emerson.desafiovotacao.repository.topic;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.emerson.desafiovotacao.domain.topic.Topic;
import com.emerson.desafiovotacao.service.topic.dto.TopicWithOpenSessionDto;

public interface TopicRepository extends JpaRepository<Topic, UUID> {
	
	boolean existsByTitle(String title);
	
	@Query("""
		SELECT new com.emerson.desafiovotacao.service.topic.dto.TopicWithOpenSessionDto(
			t.uuid,
			t.title,
			t.description,
			t.createdAt,
			s.uuid,
			s.startTime,
			s.endTime
		)
		FROM Topic t
		LEFT JOIN TopicVotingSession s ON s.topic.uuid = t.uuid
		WHERE s.startTime = (
			SELECT MAX(sub.startTime)
			FROM TopicVotingSession sub
			WHERE sub.topic.uuid = t.uuid
		)
		OR s.uuid IS NULL
	""")
	Page<TopicWithOpenSessionDto> findAllTopicsWithOpenVotingSession(Pageable page);

}
