package com.emerson.desafiovotacao.web.topic;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.emerson.desafiovotacao.domain.topic.Topic;
import com.emerson.desafiovotacao.domain.topic.TopicVotingSession;
import com.emerson.desafiovotacao.service.topic.TopicService;
import com.emerson.desafiovotacao.service.topic.TopicVotingSessionService;
import com.emerson.desafiovotacao.service.topic.dto.TopicDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/topic")
public class TopicController {
	
	@Autowired
	private TopicService service;
	
	@Autowired
	private TopicVotingSessionService topicVotingSessionService;
	
	@PostMapping
	public ResponseEntity<Topic> create(@RequestBody @Valid TopicDto topic) {
		return new ResponseEntity<>(this.service.create(topic), HttpStatus.CREATED);
	}
	
	@PostMapping("/{topicUuid}/startVotingSession")
	public ResponseEntity<TopicVotingSession> startVotingSession(@PathVariable UUID topicUuid, 
													@RequestParam(defaultValue = "1") Integer durationInMinutes) 
	{
		return new ResponseEntity<>(
				this.topicVotingSessionService.startVotingSession(topicUuid, durationInMinutes), 
				HttpStatus.CREATED
		);
	}

}
