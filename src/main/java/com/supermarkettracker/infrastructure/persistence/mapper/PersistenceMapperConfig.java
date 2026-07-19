package com.supermarkettracker.infrastructure.persistence.mapper;
import org.mapstruct.MapperConfig; import org.mapstruct.ReportingPolicy;
@MapperConfig(componentModel="spring", unmappedTargetPolicy=ReportingPolicy.ERROR) public interface PersistenceMapperConfig {}
