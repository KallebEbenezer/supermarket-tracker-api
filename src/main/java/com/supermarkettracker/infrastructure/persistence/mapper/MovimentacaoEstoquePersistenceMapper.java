package com.supermarkettracker.infrastructure.persistence.mapper;
import com.supermarkettracker.domain.model.MovimentacaoEstoque; import com.supermarkettracker.infrastructure.persistence.converter.DomainValueConverter; import com.supermarkettracker.infrastructure.persistence.entity.MovimentacaoEstoqueEntity; import org.mapstruct.*;
@Mapper(config=PersistenceMapperConfig.class, uses=DomainValueConverter.class) public interface MovimentacaoEstoquePersistenceMapper { MovimentacaoEstoqueEntity toEntity(MovimentacaoEstoque source); MovimentacaoEstoque toDomain(MovimentacaoEstoqueEntity source); }
