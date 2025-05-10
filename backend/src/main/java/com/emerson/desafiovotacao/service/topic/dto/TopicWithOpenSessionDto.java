package com.emerson.desafiovotacao.service.topic.dto;

import java.time.Instant;
import java.util.UUID;

public record TopicWithOpenSessionDto(
		UUID topicUuid,
		String topicTitle,
		String topicDescription,
		Instant createdAt,
		UUID sessionUuid,
		Instant sessionStart,
		Instant sessionEnd
) {

}
