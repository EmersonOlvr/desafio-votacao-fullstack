package com.emerson.desafiovotacao.web.vote;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.emerson.desafiovotacao.service.vote.VoteResultsService;
import com.emerson.desafiovotacao.service.vote.VoteService;
import com.emerson.desafiovotacao.service.vote.dto.TopicVoteResultsDto;

@RestController
@RequestMapping("/api/v1/vote")
public class VoteController {
	
	@Autowired
	private VoteService service;
	
	@Autowired
	private VoteResultsService voteResultsService;
	
	@PostMapping("/topic/{topicUuid}")
	public ResponseEntity<Object> voteByTopic(@PathVariable UUID topicUuid, @RequestParam String cpf, @RequestParam Boolean vote) {
		this.service.voteByTopicUuid(topicUuid, cpf, vote);
		return ResponseEntity.noContent().build();
	}
	
	@PostMapping("/votingSession/{topicVotingSessionUuid}")
	public ResponseEntity<Object> voteByVotingSession(@PathVariable UUID topicVotingSessionUuid, @RequestParam String cpf, @RequestParam Boolean vote) {
		this.service.voteByVotingSessionUuid(topicVotingSessionUuid, cpf, vote);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/topic/{topicUuid}/results")
	public ResponseEntity<TopicVoteResultsDto> getResultsByTopic(@PathVariable UUID topicUuid) {
		return ResponseEntity.ok(this.voteResultsService.getResultsByTopicUuid(topicUuid));
	}
	
}
