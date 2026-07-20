package com.supermarkettracker.infrastructure.persistence.entity;

import com.supermarkettracker.domain.model.enums.PapelUsuario;
import jakarta.persistence.*; import org.hibernate.annotations.JdbcTypeCode; import org.hibernate.type.SqlTypes; import java.time.Instant; import java.util.UUID;

@Entity @Table(name = "credencial")
public class CredencialEntity {
    @Id public UUID id;
    @Column(nullable = false, unique = true, length = 255) public String email;
    @Column(name = "senha_hash", nullable = false, length = 100) public String senhaHash;
    @Column(name = "usuario_id", nullable = false) public UUID usuarioId;
    @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.NAMED_ENUM) @Column(nullable = false, columnDefinition = "papel_usuario") public PapelUsuario papel;
    @Column(nullable = false) public boolean ativo;
    @Column(name = "token_reset_senha", length = 120) public String tokenResetSenha;
    @Column(name = "expira_reset_em") public Instant expiraResetEm;
    @Column(name = "created_at", nullable = false) public Instant criadoEm;
    @Column(name = "updated_at", nullable = false) public Instant atualizadoEm;
    public CredencialEntity() { }
}
