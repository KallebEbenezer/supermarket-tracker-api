package com.supermarkettracker.infrastructure.persistence.adapter;

import com.supermarkettracker.domain.model.Pagamento;
import com.supermarkettracker.domain.model.PagamentoCartao;
import com.supermarkettracker.domain.model.PagamentoPix;
import com.supermarkettracker.domain.model.enums.StatusPagamento;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.PagamentoRepository;
import com.supermarkettracker.infrastructure.persistence.entity.PagamentoPixEntity;
import com.supermarkettracker.infrastructure.persistence.mapper.CadastrosPersistenceMapper;
import com.supermarkettracker.infrastructure.persistence.mapper.PagamentoPersistenceMapper;
import com.supermarkettracker.infrastructure.persistence.repository.PagamentoCartaoJpaRepository;
import com.supermarkettracker.infrastructure.persistence.repository.PagamentoJpaRepository;
import com.supermarkettracker.infrastructure.persistence.repository.PagamentoPixJpaRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class PagamentoRepositoryJpaAdapter implements PagamentoRepository {
    private final PagamentoJpaRepository pagamentos;
    private final PagamentoPixJpaRepository pagamentosPix;
    private final PagamentoCartaoJpaRepository pagamentosCartao;
    private final PagamentoPersistenceMapper pagamentoMapper;
    private final CadastrosPersistenceMapper cadastrosMapper;

    public PagamentoRepositoryJpaAdapter(PagamentoJpaRepository pagamentos, PagamentoPixJpaRepository pagamentosPix,
            PagamentoCartaoJpaRepository pagamentosCartao, PagamentoPersistenceMapper pagamentoMapper,
            CadastrosPersistenceMapper cadastrosMapper) {
        this.pagamentos = pagamentos;
        this.pagamentosPix = pagamentosPix;
        this.pagamentosCartao = pagamentosCartao;
        this.pagamentoMapper = pagamentoMapper;
        this.cadastrosMapper = cadastrosMapper;
    }

    @Override
    public Pagamento salvar(Pagamento pagamento) {
        return pagamentoMapper.toDomain(pagamentos.save(pagamentoMapper.toEntity(pagamento)));
    }

    @Override
    public Pagamento salvarPix(Pagamento pagamento, PagamentoPix pix) {
        Pagamento salvo = salvar(pagamento);
        pagamentosPix.save(cadastrosMapper.toEntity(pix));
        return salvo;
    }

    @Override
    public Pagamento salvarCartao(Pagamento pagamento, PagamentoCartao cartao) {
        Pagamento salvo = salvar(pagamento);
        pagamentosCartao.save(cadastrosMapper.toEntity(cartao));
        return salvo;
    }

    @Override
    public Optional<Pagamento> buscarPorId(Identificador id) {
        return pagamentos.findById(id.valor()).map(pagamentoMapper::toDomain);
    }

    @Override
    public List<Pagamento> listarPorVenda(Identificador vendaId) {
        return pagamentos.findByVendaIdOrderByCriadoEmAsc(vendaId.valor()).stream()
                .map(pagamentoMapper::toDomain).toList();
    }

    @Override
    public void atualizarStatus(Identificador pagamentoId, StatusPagamento status, Instant recebidoEm) {
        pagamentos.findById(pagamentoId.valor()).ifPresent(entity -> {
            entity.status = status;
            entity.recebidoEm = recebidoEm;
            entity.atualizadoEm = Instant.now();
            pagamentos.save(entity);
        });
    }

    @Override
    public Optional<PagamentoPix> buscarPixPorGatewayTransacaoId(String gatewayTransacaoId) {
        return pagamentosPix.findByGatewayTransacaoId(gatewayTransacaoId)
                .map(entity -> cadastrosMapper.toDomain(entity));
    }

    @Override
    public Optional<PagamentoPix> buscarPixPorVendaId(Identificador vendaId) {
        return pagamentosPix.findByVendaId(vendaId.valor())
                .map(entity -> cadastrosMapper.toDomain(entity));
    }
}
