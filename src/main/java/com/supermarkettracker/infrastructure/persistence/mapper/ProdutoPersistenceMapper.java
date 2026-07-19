package com.supermarkettracker.infrastructure.persistence.mapper;
import com.supermarkettracker.domain.model.Produto; import com.supermarkettracker.infrastructure.persistence.converter.DomainValueConverter; import com.supermarkettracker.infrastructure.persistence.entity.ProdutoEntity; import org.mapstruct.*;
@Mapper(config=PersistenceMapperConfig.class, uses=DomainValueConverter.class) public interface ProdutoPersistenceMapper { ProdutoEntity toEntity(Produto source); Produto toDomain(ProdutoEntity source); }
