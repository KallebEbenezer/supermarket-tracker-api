package com.supermarkettracker.infrastructure.persistence.mapper;
import com.supermarkettracker.domain.model.Empresa; import com.supermarkettracker.infrastructure.persistence.converter.DomainValueConverter; import com.supermarkettracker.infrastructure.persistence.entity.EmpresaEntity; import org.mapstruct.*;
@Mapper(config=PersistenceMapperConfig.class, uses=DomainValueConverter.class) public interface EmpresaPersistenceMapper { EmpresaEntity toEntity(Empresa source); Empresa toDomain(EmpresaEntity source); }
