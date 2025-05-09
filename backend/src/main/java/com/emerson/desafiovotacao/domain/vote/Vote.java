package com.emerson.desafiovotacao.domain.vote;

import java.time.Instant;

import org.hibernate.annotations.DynamicInsert;

import com.emerson.desafiovotacao.domain.UUIDEntity;
import com.emerson.desafiovotacao.domain.topic.TopicVotingSession;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "vote")
@DynamicInsert
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Vote extends UUIDEntity {
	
	private static final long serialVersionUID = -6702580382103258760L;
	
	@ManyToOne
	@JoinColumn(nullable = false)
	private TopicVotingSession topicVotingSession;
	
	@Column(nullable = false, length = 11)
	private String cpf;
	
	@Column(nullable = false)
	private Boolean vote;

	@Column(nullable = false, updatable = false)
	private Instant votedAt;

}
