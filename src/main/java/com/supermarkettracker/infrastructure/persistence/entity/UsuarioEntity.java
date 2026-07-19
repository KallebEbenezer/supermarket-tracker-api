package com.supermarkettracker.infrastructure.persistence.entity;
import com.supermarkettracker.domain.model.enums.StatusAtivo;
import jakarta.persistence.*; import org.hibernate.annotations.JdbcTypeCode; import org.hibernate.type.SqlTypes;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name="usuario") public class UsuarioEntity {
 @Id public UUID id; @Column(name="auth_user_id",nullable=false,unique=true) public UUID authUserId;
 @Column(nullable=false,length=160) public String nome; @Column(nullable=false,unique=true,length=255) public String email; @Column(length=30) public String telefone;
 @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.NAMED_ENUM) @Column(nullable=false,columnDefinition="status_ativo") public StatusAtivo status;
 @Column(name="created_at",nullable=false) public Instant criadoEm; @Column(name="updated_at",nullable=false) public Instant atualizadoEm; public UsuarioEntity() {}
}
