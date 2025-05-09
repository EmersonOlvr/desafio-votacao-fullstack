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

import com.emerson.desafiovotacao.exception.ValidationHandler;
import com.emerson.desafiovotacao.service.vote.VoteResultsService;
import com.emerson.desafiovotacao.service.vote.VoteService;
import com.emerson.desafiovotacao.service.vote.dto.TopicVoteResultsDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/vote")
@Tag(name = "Votes", description = "Votação em pautas e obtenção de resultados")
public class VoteController {
	
	@Autowired
	private VoteService service;
	
	@Autowired
	private VoteResultsService voteResultsService;
	
	@PostMapping("/topic/{topicUuid}")
	@Operation(summary = "Votar em uma pauta usando o ID da pauta")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Voto registrado com sucesso", content = @Content),
		@ApiResponse(
			responseCode = "404", 
			description = "Sessão de votação não encontrada",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ValidationHandler.ErrorResponse.class)
			)
		),
		@ApiResponse(
			responseCode = "409", 
			description = "Associado já votou nesta pauta",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ValidationHandler.ErrorResponse.class)
			)
		)
	})
	public ResponseEntity<Object> voteByTopic(
			@PathVariable @Parameter(description = "ID da pauta") UUID topicUuid, 
			@RequestParam @Parameter(description = "CPF do associado") String cpf, 
			@RequestParam @Parameter(description = "Voto (true para favorável, false para contra)") Boolean vote) 
	{
		this.service.voteByTopicUuid(topicUuid, cpf, vote);
		return ResponseEntity.noContent().build();
	}
	
	@PostMapping("/votingSession/{topicVotingSessionUuid}")
	@Operation(summary = "Votar usando o UUID da sessão de votação")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Voto registrado com sucesso", content = @Content),
		@ApiResponse(
			responseCode = "404", 
			description = "Sessão de votação não encontrada",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ValidationHandler.ErrorResponse.class)
			)
		),
		@ApiResponse(
			responseCode = "409", 
			description = "Associado já votou nesta pauta",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ValidationHandler.ErrorResponse.class)
			)
		)
	})
	public ResponseEntity<Object> voteByVotingSession(
			@PathVariable @Parameter(description = "ID da sessão de votação") UUID topicVotingSessionUuid, 
			@RequestParam @Parameter(description = "CPF do associado") String cpf, 
			@RequestParam @Parameter(description = "Voto (true para favorável, false para contra)") Boolean vote) 
	{
		this.service.voteByVotingSessionUuid(topicVotingSessionUuid, cpf, vote);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/topic/{topicUuid}/results")
	@Operation(summary = "Consulta o resultado da votação de uma pauta")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Resultado da pauta retornado com sucesso",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = TopicVoteResultsDto.class)
			)
		),
		@ApiResponse(
			responseCode = "404",
			description = "Pauta não encontrada",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ValidationHandler.ErrorResponse.class)
			)
		)
	})
	public ResponseEntity<TopicVoteResultsDto> getResultsByTopic(@PathVariable @Parameter(description = "ID da pauta") UUID topicUuid) {
		return ResponseEntity.ok(this.voteResultsService.getResultsByTopicUuid(topicUuid));
	}
	
}
