package com.supermarkettracker.application.service;

/** Lançada quando um token JWT é inválido, adulterado ou expirado. */
public class TokenInvalidoException extends RuntimeException {
    public TokenInvalidoException(String mensagem) {
        super(mensagem);
    }
}
