package com.supermarkettracker.infrastructure.persistence.adapter;
import com.supermarkettracker.domain.model.ItemVenda;
import com.supermarkettracker.domain.model.Venda;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.VendaRepository;
import com.supermarkettracker.infrastructure.persistence.entity.VendaEntity;
import com.supermarkettracker.infrastructure.persistence.mapper.VendaPersistenceMapper;
import com.supermarkettracker.infrastructure.persistence.repository.ItemVendaJpaRepository;
import com.supermarkettracker.infrastructure.persistence.repository.VendaJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class VendaRepositoryJpaAdapter implements VendaRepository {
    private final VendaJpaRepository vendas;
    private final ItemVendaJpaRepository itens;
    private final VendaPersistenceMapper mapper;

    public VendaRepositoryJpaAdapter(VendaJpaRepository vendas, ItemVendaJpaRepository itens,
            VendaPersistenceMapper mapper) {
        this.vendas = vendas;
        this.itens = itens;
        this.mapper = mapper;
    }

    public Venda salvar(Venda venda) { return mapper.toDomain(vendas.save(mapper.toEntity(venda))); }
    public Optional<Venda> buscarPorId(Identificador id) { return vendas.findById(id.valor()).map(mapper::toDomain); }
    public ItemVenda salvarItem(ItemVenda item) { return mapper.toDomain(itens.save(mapper.toEntity(item))); }

    public List<ItemVenda> listarItens(Identificador vendaId) {
        return itens.findByVendaIdOrderByNumeroAsc(vendaId.valor()).stream().map(mapper::toDomain).toList();
    }

    public List<Venda> listarPorEmpresa(Identificador empresaId, Identificador lojaId, int limite) {
        List<VendaEntity> entidades = lojaId == null
                ? vendas.findByEmpresaIdOrderByCriadoEmDesc(empresaId.valor())
                : vendas.findByEmpresaIdAndLojaIdOrderByCriadoEmDesc(empresaId.valor(), lojaId.valor());
        return entidades.stream().limit(limite).map((VendaEntity e) -> mapper.toDomain(e)).toList();
    }

    public Optional<Long> findMaxNumeroByEmpresaId(Identificador empresaId) {
        return vendas.findMaxNumeroByEmpresaId(empresaId.valor());
    }
}
