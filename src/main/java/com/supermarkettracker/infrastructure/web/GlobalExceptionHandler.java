package com.supermarkettracker.infrastructure.web;

import com.supermarkettracker.application.service.TokenInvalidoException;
import com.supermarkettracker.domain.exception.ConflitoDeDominioException;
import com.supermarkettracker.domain.exception.CredenciaisInvalidasException;
import com.supermarkettracker.domain.exception.EntidadeNaoEncontradaException;
import com.supermarkettracker.domain.exception.RegraDeDominioException;
import com.supermarkettracker.domain.exception.TokenRedefinicaoInvalidoException;
import com.supermarkettracker.infrastructure.web.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    ResponseEntity<ErrorResponse> handleNotFound(EntidadeNaoEncontradaException exception, HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", exception.getMessage(), Map.of(), request);
    }

    @ExceptionHandler(ConflitoDeDominioException.class)
    ResponseEntity<ErrorResponse> handleConflict(ConflitoDeDominioException exception, HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, "DOMAIN_CONFLICT", exception.getMessage(), Map.of(), request);
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    ResponseEntity<ErrorResponse> handleCredenciaisInvalidas(CredenciaisInvalidasException exception, HttpServletRequest request) {
        return error(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", exception.getMessage(), Map.of(), request);
    }

    @ExceptionHandler(TokenInvalidoException.class)
    ResponseEntity<ErrorResponse> handleTokenInvalido(TokenInvalidoException exception, HttpServletRequest request) {
        return error(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", exception.getMessage(), Map.of(), request);
    }

    @ExceptionHandler(TokenRedefinicaoInvalidoException.class)
    ResponseEntity<ErrorResponse> handleTokenRedefinicao(TokenRedefinicaoInvalidoException exception, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, "INVALID_RESET_TOKEN", exception.getMessage(), Map.of(), request);
    }

    @ExceptionHandler(RegraDeDominioException.class)
    ResponseEntity<ErrorResponse> handleBusinessRule(RegraDeDominioException exception, HttpServletRequest request) {
        return error(HttpStatus.UNPROCESSABLE_ENTITY, "BUSINESS_RULE_VIOLATION", exception.getMessage(), Map.of(), request);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class,
            MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class, IllegalArgumentException.class})
    ResponseEntity<ErrorResponse> handleBadRequest(Exception exception, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "Dados de entrada inválidos", validationErrors(exception), request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException exception, HttpServletRequest request) {
        LOGGER.warn("Violação de integridade no traceId={}", traceId());
        return error(HttpStatus.CONFLICT, "DATA_INTEGRITY_VIOLATION", "A operação viola uma restrição de dados", Map.of(), request);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> handleUnexpected(Exception exception, HttpServletRequest request) {
        LOGGER.error("Erro inesperado no traceId={}", traceId(), exception);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Ocorreu um erro interno", Map.of(), request);
    }

    private ResponseEntity<ErrorResponse> error(HttpStatus status, String code, String message,
            Map<String, String> validationErrors, HttpServletRequest request) {
        return ResponseEntity.status(status).body(new ErrorResponse(Instant.now(), status.value(), false, code,
                message, request.getRequestURI(), traceId(), validationErrors));
    }

    private Map<String, String> validationErrors(Exception exception) {
        if (exception instanceof MethodArgumentNotValidException validationException) {
            return validationException.getBindingResult().getFieldErrors().stream().collect(Collectors.toMap(
                    FieldError::getField, error -> error.getDefaultMessage(), (first, ignored) -> first, LinkedHashMap::new));
        }
        if (exception instanceof ConstraintViolationException validationException) {
            return validationException.getConstraintViolations().stream().collect(Collectors.toMap(
                    violation -> violation.getPropertyPath().toString(), violation -> violation.getMessage(),
                    (first, ignored) -> first, LinkedHashMap::new));
        }
        return Map.of();
    }

    private String traceId() { return MDC.get("traceId"); }
}
