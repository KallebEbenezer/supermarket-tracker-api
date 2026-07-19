package com.supermarkettracker.infrastructure.persistence.entity;

import com.supermarkettracker.domain.model.enums.StatusEmpresa;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "empresa")
public class EmpresaEntity {
  @Id public UUID id;
  @Column(name="razao_social", nullable=false, length=160) public String razaoSocial;
  @Column(name="nome_fantasia", nullable=false, length=160) public String nomeFantasia;
  @Column(length=14) public String cnpj;
  @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.NAMED_ENUM) @Column(nullable=false, columnDefinition="status_empresa") public StatusEmpresa status;
  @Column(name="created_at", nullable=false) public Instant criadoEm;
  @Column(name="updated_at", nullable=false) public Instant atualizadoEm;
  public EmpresaEntity() {}
}
