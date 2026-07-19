package com.supermarkettracker.infrastructure.persistence.mapper;

import com.supermarkettracker.domain.model.Caixa;
import com.supermarkettracker.domain.model.SessaoCaixa;
import com.supermarkettracker.infrastructure.persistence.converter.DomainValueConverter;
import com.supermarkettracker.infrastructure.persistence.entity.CaixaEntity;
import com.supermarkettracker.infrastructure.persistence.entity.SessaoCaixaEntity;
import org.mapstruct.Mapper;

@Mapper(config = PersistenceMapperConfig.class, uses = DomainValueConverter.class)
public interface CaixaPersistenceMapper {
    CaixaEntity toEntity(Caixa source);
    Caixa toDomain(CaixaEntity source);
    SessaoCaixaEntity toEntity(SessaoCaixa source);
    SessaoCaixa toDomain(SessaoCaixaEntity source);
}
