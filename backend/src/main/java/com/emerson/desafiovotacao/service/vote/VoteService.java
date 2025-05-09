package com.emerson.desafiovotacao.service.vote;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.emerson.desafiovotacao.domain.topic.TopicVotingSession;
import com.emerson.desafiovotacao.domain.vote.Vote;
import com.emerson.desafiovotacao.exception.http.ConflictException;
import com.emerson.desafiovotacao.exception.http.NotFoundTopicVotingSessionByIdException;
import com.emerson.desafiovotacao.exception.http.NotFoundTopicVotingSessionByTopicException;
import com.emerson.desafiovotacao.repository.topic.TopicVotingSessionRepository;
import com.emerson.desafiovotacao.repository.vote.VoteRepository;

@Service
public class VoteService {
	
	@Autowired
	private VoteRepository repository;
	
	@Autowired
	private TopicVotingSessionRepository votingSessionRepository;

	public void voteByTopicUuid(UUID topicUuid, String cpf, boolean vote) {
		Instant now = Instant.now();
		TopicVotingSession topicVotingSession = this.votingSessionRepository.findTopByTopicUuidAndEndTimeGreaterThanOrderByStartTimeDesc(topicUuid, now)
																			.orElseThrow(NotFoundTopicVotingSessionByTopicException::new);
		
		this.vote(topicVotingSession, cpf, vote);
	}

	public void voteByVotingSessionUuid(UUID topicVotingSessionUuid, String cpf, Boolean vote) {
		Instant now = Instant.now();
		TopicVotingSession topicVotingSession = this.votingSessionRepository.findByUuidAndEndTimeGreaterThan(topicVotingSessionUuid, now)
																			.orElseThrow(NotFoundTopicVotingSessionByIdException::new);
		
		this.vote(topicVotingSession, cpf, vote);
	}
	
	private Vote vote(TopicVotingSession topicVotingSession, String cpf, boolean vote) {
		if (this.repository.existsByTopicVotingSessionTopicUuidAndCpf(topicVotingSession.getTopic().getUuid(), cpf))
			throw new ConflictException("O associado já votou nesta pauta. Só é permitido votar uma vez por pauta.");
		
		Instant now = Instant.now();
		return this.repository.save(Vote.builder()
										.topicVotingSession(topicVotingSession)
										.cpf(cpf)
										.vote(vote)
										.votedAt(now)
										.build()
		);
	}

}
