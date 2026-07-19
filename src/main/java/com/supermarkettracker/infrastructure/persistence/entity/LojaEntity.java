package com.supermarkettracker.infrastructure.persistence.entity;
import com.supermarkettracker.domain.model.enums.StatusAtivo;
import jakarta.persistence.*; import org.hibernate.annotations.JdbcTypeCode; import org.hibernate.type.SqlTypes;
import org.hibernate.annotations.ColumnTransformer;
import java.time.Instant; import java.util.UUID;
import com.supermarkettracker.domain.model.valueobject.Endereco;
import com.supermarkettracker.infrastructure.persistence.converter.EnderecoJsonConverter;
@Entity @Table(name="loja") public class LojaEntity {
 @Id public UUID id; @Column(name="empresa_id",nullable=false) public UUID empresaId; @Column(nullable=false,length=30) public String codigo; @Column(nullable=false,length=160) public String nome; @Column(length=14) public String cnpj;
 @Convert(converter = EnderecoJsonConverter.class)
 @ColumnTransformer(write = "?::jsonb")
 @Column(columnDefinition="jsonb") public Endereco endereco; @Column(length=30) public String telefone;
 @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.NAMED_ENUM) @Column(nullable=false,columnDefinition="status_ativo") public StatusAtivo status;
 @Column(name="created_at",nullable=false) public Instant criadoEm; @Column(name="updated_at",nullable=false) public Instant atualizadoEm; public LojaEntity() {}
}
