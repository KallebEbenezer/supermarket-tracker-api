package com.supermarkettracker.infrastructure.persistence.adapter;

import com.supermarkettracker.domain.model.ContaBancaria;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.ContaBancariaRepository;
import com.supermarkettracker.infrastructure.persistence.mapper.ContaBancariaPersistenceMapper;
import com.supermarkettracker.infrastructure.persistence.repository.ContaBancariaJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class ContaBancariaRepositoryJpaAdapter implements ContaBancariaRepository {
    private final ContaBancariaJpaRepository jpa;
    private final ContaBancariaPersistenceMapper mapper;

    public ContaBancariaRepositoryJpaAdapter(ContaBancariaJpaRepository jpa, ContaBancariaPersistenceMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    public ContaBancaria salvar(ContaBancaria conta) { return mapper.toDomain(jpa.save(mapper.toEntity(conta))); }
    public Optional<ContaBancaria> buscarPorId(Identificador id) { return jpa.findById(id.valor()).map(mapper::toDomain); }
    public List<ContaBancaria> listarPorEmpresa(Identificador empresaId) {
        return jpa.findByEmpresaIdOrderByPrincipalDescBancoNomeAsc(empresaId.valor()).stream().map(mapper::toDomain).toList();
    }
    public void excluirPorId(Identificador id) { jpa.deleteById(id.valor()); }
}
