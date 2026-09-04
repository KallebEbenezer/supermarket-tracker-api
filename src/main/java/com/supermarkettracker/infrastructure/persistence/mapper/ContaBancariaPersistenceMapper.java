package com.supermarkettracker.infrastructure.persistence.mapper;

import com.supermarkettracker.domain.model.ContaBancaria;
import com.supermarkettracker.infrastructure.persistence.converter.DomainValueConverter;
import com.supermarkettracker.infrastructure.persistence.entity.ContaBancariaEntity;
import org.mapstruct.Mapper;

@Mapper(config = PersistenceMapperConfig.class, uses = DomainValueConverter.class)
public interface ContaBancariaPersistenceMapper {
    ContaBancariaEntity toEntity(ContaBancaria source);
    ContaBancaria toDomain(ContaBancariaEntity source);
}
