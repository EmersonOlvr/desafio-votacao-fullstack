package com.emerson.desafiovotacao.service.topic;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.emerson.desafiovotacao.domain.topic.Topic;
import com.emerson.desafiovotacao.exception.http.ConflictException;
import com.emerson.desafiovotacao.exception.http.TopicVotingSessionNotFoundByIdException;
import com.emerson.desafiovotacao.repository.topic.TopicRepository;
import com.emerson.desafiovotacao.service.topic.dto.TopicDto;
import com.emerson.desafiovotacao.service.topic.dto.TopicWithOpenSessionDto;

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
	 * Retorna uma lista paginada de pautas, cada uma contendo a sessão de votação mais recente,
	 * caso exista. A lista pode ser ordenada dinamicamente com base nos parâmetros fornecidos.
	 * 
	 * Este método garante que todas as pautas sejam incluídas no resultado, mesmo aquelas que
	 * ainda não possuem sessões de votação vinculadas.
	 *
	 * @param page Número da página a ser retornada (começando em 1).
	 * @param size Quantidade de elementos por página.
	 * @param order Direção da ordenação: "ASC" para ascendente ou "DESC" para descendente.
	 * @param orderBy Nome do campo da pauta pelo qual a ordenação será realizada (ex: "createdAt", "title").
	 * @return Página contendo as pautas com suas respectivas sessões de votação mais recentes (se existirem).
	 */
	public Page<TopicWithOpenSessionDto> list(Integer page, Integer size, String order, String orderBy) {
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Direction.fromString(order), orderBy));
		return this.repository.findAllTopicsWithOpenVotingSession(pageable);
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
