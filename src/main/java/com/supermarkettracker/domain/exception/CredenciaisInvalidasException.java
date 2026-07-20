package com.supermarkettracker.domain.exception;

/** Lançada quando e-mail/senha estão incorretos ou a credencial está inativa. */
public class CredenciaisInvalidasException extends RuntimeException {
    public CredenciaisInvalidasException(String mensagem) {
        super(mensagem);
    }
}
