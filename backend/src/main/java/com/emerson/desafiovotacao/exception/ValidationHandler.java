package com.emerson.desafiovotacao.exception;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ValidationHandler {
	
	public record FieldErrorResponse(String field, String message) {}
	public record ValidationErrorResponse(
		Instant timestamp,
		int status,
		String error,
		String message,
		String path,
		List<FieldErrorResponse> errors
	) {}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ValidationErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
		List<FieldErrorResponse> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
																		.map(err -> new FieldErrorResponse(err.getField(), err.getDefaultMessage()))
																		.sorted(Comparator
																				.comparing(FieldErrorResponse::field)
																				.thenComparing(FieldErrorResponse::message)
																		)
																		.toList();

		ValidationErrorResponse response = new ValidationErrorResponse(
				Instant.now(),
				HttpStatus.BAD_REQUEST.value(), 
				HttpStatus.BAD_REQUEST.getReasonPhrase(),
				"Erro de validação nos campos", 
				request.getRequestURI(), 
				fieldErrors
		);

		return ResponseEntity.badRequest().body(response);
	}
	
}