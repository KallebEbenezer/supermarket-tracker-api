package com.supermarkettracker.infrastructure.web;

import com.supermarkettracker.domain.exception.*;
import com.supermarkettracker.infrastructure.web.dto.ErroResponse;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    ResponseEntity<ErroResponse> notFound(EntidadeNaoEncontradaException exception) { return response(HttpStatus.NOT_FOUND, exception.getMessage(), Map.of()); }
    @ExceptionHandler({ConflitoDeDominioException.class, RegraDeDominioException.class})
    ResponseEntity<ErroResponse> conflict(RuntimeException exception) { return response(HttpStatus.CONFLICT, exception.getMessage(), Map.of()); }
    @ExceptionHandler({IllegalArgumentException.class})
    ResponseEntity<ErroResponse> badRequest(RuntimeException exception) { return response(HttpStatus.BAD_REQUEST, exception.getMessage(), Map.of()); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErroResponse> validation(MethodArgumentNotValidException exception) {
        Map<String, String> campos = exception.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(error -> error.getField(), error -> error.getDefaultMessage(), (first, ignored) -> first));
        return response(HttpStatus.BAD_REQUEST, "Dados de entrada inválidos", campos);
    }
    private ResponseEntity<ErroResponse> response(HttpStatus status, String mensagem, Map<String, String> campos) {
        return ResponseEntity.status(status).body(new ErroResponse(Instant.now(), status.value(), mensagem, campos));
    }
}
