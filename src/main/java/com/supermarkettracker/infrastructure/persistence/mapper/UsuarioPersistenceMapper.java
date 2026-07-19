package com.supermarkettracker.infrastructure.persistence.mapper;
import com.supermarkettracker.domain.model.Usuario; import com.supermarkettracker.infrastructure.persistence.converter.DomainValueConverter; import com.supermarkettracker.infrastructure.persistence.entity.UsuarioEntity; import org.mapstruct.*;
@Mapper(config=PersistenceMapperConfig.class, uses=DomainValueConverter.class) public interface UsuarioPersistenceMapper { UsuarioEntity toEntity(Usuario source); Usuario toDomain(UsuarioEntity source); }
