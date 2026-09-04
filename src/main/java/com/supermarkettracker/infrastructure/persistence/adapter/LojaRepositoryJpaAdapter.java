package com.supermarkettracker.infrastructure.persistence.adapter;
import com.supermarkettracker.domain.model.Loja;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.LojaRepository;
import com.supermarkettracker.infrastructure.persistence.mapper.LojaPersistenceMapper;
import com.supermarkettracker.infrastructure.persistence.repository.LojaJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class LojaRepositoryJpaAdapter implements LojaRepository {
    private final LojaJpaRepository jpa;
    private final LojaPersistenceMapper mapper;

    public LojaRepositoryJpaAdapter(LojaJpaRepository jpa, LojaPersistenceMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    public Loja salvar(Loja loja) { return mapper.toDomain(jpa.save(mapper.toEntity(loja))); }
    public Optional<Loja> buscarPorId(Identificador id) { return jpa.findById(id.valor()).map(mapper::toDomain); }
    public List<Loja> listarPorEmpresa(Identificador empresaId) {
        return jpa.findByEmpresaIdOrderByNomeAsc(empresaId.valor()).stream().map(mapper::toDomain).toList();
    }
    public void excluirPorId(Identificador id) { jpa.deleteById(id.valor()); }
}
