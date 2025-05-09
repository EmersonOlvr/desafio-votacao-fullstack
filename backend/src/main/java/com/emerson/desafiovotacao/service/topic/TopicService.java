package com.emerson.desafiovotacao.service.topic;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.emerson.desafiovotacao.domain.topic.Topic;
import com.emerson.desafiovotacao.exception.http.ConflictException;
import com.emerson.desafiovotacao.exception.http.NotFoundTopicVotingSessionByIdException;
import com.emerson.desafiovotacao.repository.topic.TopicRepository;
import com.emerson.desafiovotacao.service.topic.dto.TopicDto;

@Service
public class TopicService {
	
	@Autowired
	private TopicRepository repository;
	
	public Topic get(UUID uuid) {
		return this.repository.findById(uuid)
								.orElseThrow(() -> new NotFoundTopicVotingSessionByIdException());
	}
	
	public Topic create(TopicDto topicDto) {
		if (this.repository.existsByTitle(topicDto.title()))
			throw new ConflictException("Já existe uma pauta com o título informado.");
		
		Topic topic = new Topic();
		topic.setCreatedAt(Instant.now());
		BeanUtils.copyProperties(topicDto, topic);
		
		return this.repository.save(topic);
	}

}
