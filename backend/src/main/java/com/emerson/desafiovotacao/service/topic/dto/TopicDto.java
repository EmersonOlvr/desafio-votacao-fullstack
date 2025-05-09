package com.emerson.desafiovotacao.service.topic.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;

public record TopicDto(
		@NotBlank(message = "O título é obrigatório.") @Length(max = 50, message = "O título deve ter no máximo 50 caracteres.") String title, 
		@NotBlank(message = "A descrição é obrigatória.") @Length(max = 500, message = "A descrição deve ter no máximo 500 caracteres.") String description
) {

}
