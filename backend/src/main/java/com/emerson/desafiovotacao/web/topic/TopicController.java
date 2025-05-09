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
import com.emerson.desafiovotacao.exception.ValidationHandler;
import com.emerson.desafiovotacao.service.topic.TopicService;
import com.emerson.desafiovotacao.service.topic.TopicVotingSessionService;
import com.emerson.desafiovotacao.service.topic.dto.TopicDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/topic")
@Tag(name = "Topics", description = "Gerenciamento de pautas e sessões de votação")
public class TopicController {
	
	@Autowired
	private TopicService service;
	
	@Autowired
	private TopicVotingSessionService topicVotingSessionService;
	
	@PostMapping
	@Operation(summary = "Cria uma nova pauta")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "201", 
			description = "Pauta criada com sucesso",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = Topic.class)
			)
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Dados inválidos",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ValidationHandler.ValidationErrorResponse.class)
			)
		),
		@ApiResponse(
			responseCode = "409", 
			description = "Pauta já existente com o título informado",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ValidationHandler.ErrorResponse.class)
			)
		)
	})
	public ResponseEntity<Topic> create(@RequestBody @Valid @Parameter(description = "Dados da pauta") TopicDto topic) {
		return new ResponseEntity<>(this.service.create(topic), HttpStatus.CREATED);
	}
	
	@PostMapping("/{topicUuid}/startVotingSession")
	@Operation(summary = "Inicia uma nova sessão de votação para uma pauta")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "201", 
			description = "Sessão de votação iniciada",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = TopicVotingSession.class)
			)
		),
		@ApiResponse(
			responseCode = "404", 
			description = "Pauta não encontrada",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ValidationHandler.ErrorResponse.class)
			)
		),
		@ApiResponse(
			responseCode = "409", 
			description = "Sessão de votação em andamento já existente",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ValidationHandler.ErrorResponse.class)
			)
		)
	})
	public ResponseEntity<TopicVotingSession> startVotingSession(
			@PathVariable @Parameter(description = "ID da pauta") UUID topicUuid, 
			@RequestParam(defaultValue = "1") @Parameter(description = "Duração da sessão em minutos") Integer durationInMinutes) 
	{
		return new ResponseEntity<>(
				this.topicVotingSessionService.startVotingSession(topicUuid, durationInMinutes), 
				HttpStatus.CREATED
		);
	}

}
