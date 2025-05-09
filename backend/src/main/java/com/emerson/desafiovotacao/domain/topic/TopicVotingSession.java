package com.emerson.desafiovotacao.domain.topic;

import java.time.Instant;

import org.hibernate.annotations.DynamicInsert;

import com.emerson.desafiovotacao.domain.UUIDEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(name = "voting_session")
@DynamicInsert
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TopicVotingSession extends UUIDEntity {
	
	private static final long serialVersionUID = -102211429663842339L;
	
	@ManyToOne
	@JoinColumn(nullable = false)
	@JsonIgnore
	private Topic topic;

	@Column(nullable = false, updatable = false)
	private Instant startTime;

	@Column(nullable = false)
	private Instant endTime;

}
