package com.supermarkettracker.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "sumup_oauth_credential")
public class SumupOAuthCredentialEntity {
    @Id
    public short id;

    @Column(name = "refresh_token_criptografado", nullable = false)
    public String refreshTokenCriptografado;

    @Column(name = "updated_at", nullable = false)
    public Instant atualizadoEm;

    public SumupOAuthCredentialEntity() { }
}
