package com.supermarkettracker.infrastructure.persistence.mapper;

import com.supermarkettracker.domain.model.Loja;
import com.supermarkettracker.infrastructure.persistence.converter.DomainValueConverter;
import com.supermarkettracker.infrastructure.persistence.entity.LojaEntity;
import org.mapstruct.Mapper;

@Mapper(config = PersistenceMapperConfig.class, uses = DomainValueConverter.class)
public interface LojaPersistenceMapper {
    LojaEntity toEntity(Loja source);
    Loja toDomain(LojaEntity source);
}
