package com.emerson.desafiovotacao.service.vote;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.emerson.desafiovotacao.domain.topic.TopicVotingSession;
import com.emerson.desafiovotacao.domain.vote.Vote;
import com.emerson.desafiovotacao.exception.http.BadRequestException;
import com.emerson.desafiovotacao.exception.http.ConflictException;
import com.emerson.desafiovotacao.exception.http.TopicVotingSessionNotFoundByIdException;
import com.emerson.desafiovotacao.exception.http.TopicVotingSessionNotFoundByTopicException;
import com.emerson.desafiovotacao.external.CpfValidationClient;
import com.emerson.desafiovotacao.external.CpfValidationResponse;
import com.emerson.desafiovotacao.external.VoteEligibilityStatus;
import com.emerson.desafiovotacao.repository.topic.TopicVotingSessionRepository;
import com.emerson.desafiovotacao.repository.vote.VoteRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Serviço responsável pela gestão dos votos nas sessões de votação das pautas.
 * Contém métodos para registrar votos de associados em pautas específicas.
 * 
 * @author Emerson Oliveira
 */
@Service
@Slf4j
public class VoteService {
	
	@Autowired
	private VoteRepository repository;
	
	@Autowired
	private TopicVotingSessionRepository votingSessionRepository;
	
	@Autowired
	private CpfValidationClient cpfValidationClient;

	/**
	 * Registra um voto para uma pauta identificada pelo UUID.
	 * 
	 * Verifica se existe uma sessão de votação em andamento para a pauta e, caso exista,
	 * registra o voto do associado identificado pelo CPF.
	 * 
	 * @param topicUuid O identificador único da pauta para a qual o voto será registrado.
	 * @param cpf O CPF do associado que está votando.
	 * @param vote O valor do voto (true para sim, false para não).
	 * @throws TopicVotingSessionNotFoundByTopicException Caso não exista uma sessão de votação em andamento para a pauta.
	 * @throws ConflictException Caso o associado já tenha votado na pauta.
	 */
	public void voteByTopicUuid(UUID topicUuid, String cpf, boolean vote) {
		Instant now = Instant.now();
		TopicVotingSession topicVotingSession = this.votingSessionRepository.findTopByTopicUuidAndEndTimeGreaterThanOrderByStartTimeDesc(topicUuid, now)
																			.orElseThrow(TopicVotingSessionNotFoundByTopicException::new);
		
		this.vote(topicVotingSession, cpf, vote);
	}

	/**
	 * Registra um voto para uma sessão de votação identificada pelo UUID da sessão.
	 * 
	 * Verifica se existe uma sessão de votação em andamento e, caso exista,
	 * registra o voto do associado identificado pelo CPF.
	 * 
	 * @param topicVotingSessionUuid O identificador único da sessão de votação para a qual o voto será registrado.
	 * @param cpf O CPF do associado que está votando.
	 * @param vote O valor do voto (true para sim, false para não).
	 * @throws TopicVotingSessionNotFoundByIdException Caso não exista uma sessão de votação em andamento para o UUID informado.
	 * @throws ConflictException Caso o associado já tenha votado na sessão de votação.
	 */
	public void voteByVotingSessionUuid(UUID topicVotingSessionUuid, String cpf, Boolean vote) {
		Instant now = Instant.now();
		TopicVotingSession topicVotingSession = this.votingSessionRepository.findByUuidAndEndTimeGreaterThan(topicVotingSessionUuid, now)
																			.orElseThrow(TopicVotingSessionNotFoundByIdException::new);
		
		this.vote(topicVotingSession, cpf, vote);
	}
	
	/**
	 * Registra o voto de um associado em uma sessão de votação.
	 * 
	 * Verifica se o associado já votou na pauta antes de permitir o registro do voto.
	 * Caso o associado não tenha votado e o seu CPF estiver apto para votar, então 
	 * o voto é registrado no banco de dados.
	 * 
	 * @param topicVotingSession A sessão de votação na qual o voto será registrado.
	 * @param cpf O CPF do associado que está votando.
	 * @param vote O valor do voto (true para sim, false para não).
	 * @return O voto registrado no banco de dados.
	 * @throws ConflictException Caso o associado já tenha votado nesta pauta.
	 */
	private Vote vote(TopicVotingSession topicVotingSession, String cpf, boolean vote) {
		// remove caracteres deixando apenas números
		cpf = cpf.replaceAll("[^0-9]", "");
		
		if (this.repository.existsByTopicVotingSessionTopicUuidAndCpf(topicVotingSession.getTopic().getUuid(), cpf))
			throw new ConflictException("O associado já votou nesta pauta. Só é permitido votar uma vez por pauta.");
		
		CpfValidationResponse cpfValidationResponse = this.cpfValidationClient.validateCpf(cpf);
		if (!cpfValidationResponse.getStatus().equals(VoteEligibilityStatus.ABLE_TO_VOTE))
			throw new BadRequestException("O CPF informado não está apto para votar.");
		
		log.info(String.format("Computando voto do associado %s...", cpf));
		Instant now = Instant.now();
		Vote savedVote = this.repository.save(Vote.builder()
												.topicVotingSession(topicVotingSession)
												.cpf(cpf)
												.vote(vote)
												.votedAt(now)
												.build()
		);
		log.info(String.format("Voto do associado com CPF %s computado com sucesso!", cpf));
		
		return savedVote;
	}

}
