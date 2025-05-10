package com.emerson.desafiovotacao.service.topic.dto;

import java.time.Instant;
import java.util.UUID;

public record TopicWithOpenSessionDto(
		UUID id,
		String title,
		String description,
		Instant createdAt,
		UUID sessionId,
		Instant sessionStartTime,
		Instant sessionEndTime
) {

}
