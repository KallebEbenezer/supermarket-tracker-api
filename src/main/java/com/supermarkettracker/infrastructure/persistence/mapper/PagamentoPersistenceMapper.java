package com.supermarkettracker.infrastructure.persistence.mapper;
import com.supermarkettracker.domain.model.Pagamento; import com.supermarkettracker.infrastructure.persistence.converter.DomainValueConverter; import com.supermarkettracker.infrastructure.persistence.entity.PagamentoEntity; import org.mapstruct.*;
@Mapper(config=PersistenceMapperConfig.class, uses=DomainValueConverter.class) public interface PagamentoPersistenceMapper { PagamentoEntity toEntity(Pagamento source); Pagamento toDomain(PagamentoEntity source); }
