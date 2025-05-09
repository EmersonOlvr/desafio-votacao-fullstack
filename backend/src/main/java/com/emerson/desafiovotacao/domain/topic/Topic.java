package com.emerson.desafiovotacao.domain.topic;

import java.time.Instant;

import org.hibernate.annotations.DynamicInsert;

import com.emerson.desafiovotacao.domain.UUIDEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "topic")
@DynamicInsert
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Topic extends UUIDEntity {
	
	private static final long serialVersionUID = -2689830137531675652L;
	
	@Column(length = 50, nullable = false)
	private String title;
	
	@Column(columnDefinition = "TEXT", nullable = false)
	private String description;

	@Column(nullable = false, updatable = false)
	private Instant createdAt;

}
