package com.supermarkettracker.infrastructure.persistence.mapper;

import com.supermarkettracker.domain.model.Cliente;
import com.supermarkettracker.infrastructure.persistence.converter.DomainValueConverter;
import com.supermarkettracker.infrastructure.persistence.entity.ClienteEntity;
import org.mapstruct.Mapper;

@Mapper(config = PersistenceMapperConfig.class, uses = DomainValueConverter.class)
public interface ClientePersistenceMapper {
    ClienteEntity toEntity(Cliente source);
    Cliente toDomain(ClienteEntity source);
}
