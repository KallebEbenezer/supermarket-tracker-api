package com.supermarkettracker.infrastructure.persistence.mapper;

import com.supermarkettracker.domain.model.CategoriaProduto;
import com.supermarkettracker.domain.model.ContaBancaria;
import com.supermarkettracker.domain.model.DashboardCacheDiario;
import com.supermarkettracker.domain.model.EmpresaUsuario;
import com.supermarkettracker.domain.model.PagamentoCartao;
import com.supermarkettracker.domain.model.PagamentoPix;
import com.supermarkettracker.infrastructure.persistence.converter.DomainValueConverter;
import com.supermarkettracker.infrastructure.persistence.entity.CategoriaProdutoEntity;
import com.supermarkettracker.infrastructure.persistence.entity.ContaBancariaEntity;
import com.supermarkettracker.infrastructure.persistence.entity.DashboardCacheDiarioEntity;
import com.supermarkettracker.infrastructure.persistence.entity.EmpresaUsuarioEntity;
import com.supermarkettracker.infrastructure.persistence.entity.PagamentoCartaoEntity;
import com.supermarkettracker.infrastructure.persistence.entity.PagamentoPixEntity;
import org.mapstruct.Mapper;

/** Mapeamentos das tabelas auxiliares que ainda não possuem uma porta de domínio própria. */
@Mapper(config = PersistenceMapperConfig.class, uses = DomainValueConverter.class)
public interface CadastrosPersistenceMapper {
    CategoriaProdutoEntity toEntity(CategoriaProduto source);
    CategoriaProduto toDomain(CategoriaProdutoEntity source);

    ContaBancariaEntity toEntity(ContaBancaria source);
    ContaBancaria toDomain(ContaBancariaEntity source);

    EmpresaUsuarioEntity toEntity(EmpresaUsuario source);
    EmpresaUsuario toDomain(EmpresaUsuarioEntity source);

    DashboardCacheDiarioEntity toEntity(DashboardCacheDiario source);
    DashboardCacheDiario toDomain(DashboardCacheDiarioEntity source);

    PagamentoPixEntity toEntity(PagamentoPix source);
    PagamentoPix toDomain(PagamentoPixEntity source);

    PagamentoCartaoEntity toEntity(PagamentoCartao source);
    PagamentoCartao toDomain(PagamentoCartaoEntity source);
}
