package com.supermarkettracker.infrastructure.persistence.adapter;
import com.supermarkettracker.domain.model.Caixa;
import com.supermarkettracker.domain.model.SessaoCaixa;
import com.supermarkettracker.domain.model.enums.StatusSessaoCaixa;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.CaixaRepository;
import com.supermarkettracker.infrastructure.persistence.mapper.CaixaPersistenceMapper;
import com.supermarkettracker.infrastructure.persistence.repository.CaixaJpaRepository;
import com.supermarkettracker.infrastructure.persistence.repository.SessaoCaixaJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class CaixaRepositoryJpaAdapter implements CaixaRepository {
    private final CaixaJpaRepository caixas;
    private final SessaoCaixaJpaRepository sessoes;
    private final CaixaPersistenceMapper mapper;

    public CaixaRepositoryJpaAdapter(CaixaJpaRepository caixas, SessaoCaixaJpaRepository sessoes,
            CaixaPersistenceMapper mapper) {
        this.caixas = caixas;
        this.sessoes = sessoes;
        this.mapper = mapper;
    }

    public Caixa salvar(Caixa caixa) { return mapper.toDomain(caixas.save(mapper.toEntity(caixa))); }
    public Optional<Caixa> buscarPorId(Identificador id) { return caixas.findById(id.valor()).map(mapper::toDomain); }
    public List<Caixa> listarPorLoja(Identificador lojaId) { return caixas.findByLojaIdOrderByNomeAsc(lojaId.valor()).stream().map(mapper::toDomain).toList(); }
    public List<Caixa> listarPorEmpresa(Identificador empresaId) { return caixas.findByEmpresaId(empresaId.valor()).stream().map(mapper::toDomain).toList(); }
    public SessaoCaixa salvarSessao(SessaoCaixa sessao) { return mapper.toDomain(sessoes.save(mapper.toEntity(sessao))); }
    public Optional<SessaoCaixa> buscarSessaoAberta(Identificador caixaId) {
        return sessoes.findByCaixaIdAndStatus(caixaId.valor(), StatusSessaoCaixa.ABERTO).map(mapper::toDomain);
    }
    public void excluirPorId(Identificador id) { caixas.deleteById(id.valor()); }
}
