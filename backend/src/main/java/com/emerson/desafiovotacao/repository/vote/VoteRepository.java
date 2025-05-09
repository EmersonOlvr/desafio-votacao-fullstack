package com.emerson.desafiovotacao.repository.vote;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.emerson.desafiovotacao.domain.vote.Vote;

public interface VoteRepository extends JpaRepository<Vote, UUID> {
	
	boolean existsByTopicVotingSessionTopicUuidAndCpf(UUID topicVotingSession, String cpf);
	
	List<Vote> findByTopicVotingSessionTopicUuid(UUID topicUuid);

}
