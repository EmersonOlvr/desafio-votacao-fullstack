package com.emerson.desafiovotacao.external;

import java.util.Random;

import org.springframework.stereotype.Component;

import br.com.caelum.stella.validation.CPFValidator;
import lombok.extern.slf4j.Slf4j;

/**
 * Cliente responsável por simular a validação de CPF e determinar a elegibilidade para votação.
 * 
 * Esta classe utiliza validação básica de CPF (estrutura e dígitos válidos)
 * e simula aleatoriamente se o CPF pode ou não votar.
 */
@Component
@Slf4j
public class CpfValidationClient {
	
	private static final Random random = new Random();

	/**
	 * Valida um CPF e retorna o status de elegibilidade para votação.<br><br>
	 * 
	 * O método realiza duas verificações:<br>
	 * 1. Validação da estrutura do CPF usando a biblioteca Stella.<br>
	 * 2. Simulação aleatória do status de elegibilidade.
	 * 
	 * @param cpf O CPF que será validado.
	 * @return Um objeto {@link CpfValidationResponse} contendo o status de elegibilidade.
	 * @throws br.com.caelum.stella.validation.InvalidStateException se o CPF for inválido.
	 */
	public CpfValidationResponse validateCpf(String cpf) {
		// validação da estrutura do CPF
		CPFValidator cpfValidator = new CPFValidator();
		cpfValidator.assertValid(cpf);

		// simula aleatoriamente se o CPF é válido (pode votar) ou não
		boolean canVote = random.nextBoolean();
		VoteEligibilityStatus status = canVote
				? VoteEligibilityStatus.ABLE_TO_VOTE
				: VoteEligibilityStatus.UNABLE_TO_VOTE;
		
		log.info(String.format("CPF %s: %s", cpf, status));

		return new CpfValidationResponse(status);
	}

}
