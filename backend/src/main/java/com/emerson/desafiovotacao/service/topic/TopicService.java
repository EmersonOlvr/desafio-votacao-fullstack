package com.emerson.desafiovotacao.service.topic;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.emerson.desafiovotacao.domain.topic.Topic;
import com.emerson.desafiovotacao.exception.http.ConflictException;
import com.emerson.desafiovotacao.exception.http.TopicVotingSessionNotFoundByIdException;
import com.emerson.desafiovotacao.repository.topic.TopicRepository;
import com.emerson.desafiovotacao.service.topic.dto.TopicDto;

/**
 * Serviço responsável pela gestão de pautas.
 * Contém métodos para obter e criar pautas no sistema.
 * 
 * @author Emerson Oliveira
 */
@Service
public class TopicService {
	
	@Autowired
	private TopicRepository repository;
	
	/**
	 * Obtém uma pauta pelo seu ID.
	 * 
	 * @param uuid O identificador único da pauta a ser obtida.
	 * @return A pauta encontrada, se existir.
	 * @throws TopicVotingSessionNotFoundByIdException Caso a pauta não seja encontrada.
	 */
	public Topic get(UUID uuid) {
		return this.repository.findById(uuid)
								.orElseThrow(() -> new TopicVotingSessionNotFoundByIdException());
	}
	
	/**
	 * Cria uma nova pauta.
	 * 
	 * Verifica se já existe uma pauta com o título informado e, se existir, lança uma exceção de conflito.
	 * Caso contrário, cria uma nova pauta e persiste no banco de dados.
	 * 
	 * @param topicDto O objeto DTO contendo os dados da nova pauta a ser criada.
	 * @return A pauta criada e persistida no banco de dados.
	 * @throws ConflictException Caso já exista uma pauta com o título informado.
	 */
	public Topic create(TopicDto topicDto) {
		if (this.repository.existsByTitle(topicDto.title()))
			throw new ConflictException("Já existe uma pauta com o título informado.");
		
		Topic topic = new Topic();
		topic.setCreatedAt(Instant.now());
		BeanUtils.copyProperties(topicDto, topic);
		
		return this.repository.save(topic);
	}

}
