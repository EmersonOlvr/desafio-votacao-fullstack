package com.emerson.desafiovotacao.repository.topic;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.emerson.desafiovotacao.domain.topic.Topic;

public interface TopicRepository extends JpaRepository<Topic, UUID> {
	
	boolean existsByTitle(String title);

}
