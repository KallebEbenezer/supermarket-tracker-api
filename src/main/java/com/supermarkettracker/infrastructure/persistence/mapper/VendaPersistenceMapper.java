package com.supermarkettracker.infrastructure.persistence.mapper;
import com.supermarkettracker.domain.model.*; import com.supermarkettracker.infrastructure.persistence.converter.DomainValueConverter; import com.supermarkettracker.infrastructure.persistence.entity.*; import org.mapstruct.*;
@Mapper(config=PersistenceMapperConfig.class, uses=DomainValueConverter.class) public interface VendaPersistenceMapper { VendaEntity toEntity(Venda source); Venda toDomain(VendaEntity source); ItemVendaEntity toEntity(ItemVenda source); ItemVenda toDomain(ItemVendaEntity source); }
