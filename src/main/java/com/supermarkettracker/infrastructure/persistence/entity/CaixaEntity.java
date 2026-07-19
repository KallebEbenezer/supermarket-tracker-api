package com.supermarkettracker.infrastructure.persistence.entity;
import com.supermarkettracker.domain.model.enums.StatusAtivo;
import jakarta.persistence.*; import org.hibernate.annotations.JdbcTypeCode; import org.hibernate.type.SqlTypes;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name="caixa") public class CaixaEntity { @Id public UUID id; @Column(name="loja_id",nullable=false) public UUID lojaId; @Column(nullable=false,length=30) public String codigo; @Column(nullable=false,length=100) public String nome; @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.NAMED_ENUM) @Column(nullable=false,columnDefinition="status_ativo") public StatusAtivo status; @Column(name="created_at",nullable=false) public Instant criadoEm; @Column(name="updated_at",nullable=false) public Instant atualizadoEm; public CaixaEntity() {} }
