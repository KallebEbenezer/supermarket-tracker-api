package com.supermarkettracker.infrastructure.persistence.mapper;
import com.supermarkettracker.domain.model.EmpresaUsuario; import com.supermarkettracker.infrastructure.persistence.converter.DomainValueConverter; import com.supermarkettracker.infrastructure.persistence.entity.EmpresaUsuarioEntity; import org.mapstruct.*;
@Mapper(config=PersistenceMapperConfig.class, uses=DomainValueConverter.class) public interface EmpresaUsuarioPersistenceMapper { EmpresaUsuarioEntity toEntity(EmpresaUsuario source); EmpresaUsuario toDomain(EmpresaUsuarioEntity source); }
