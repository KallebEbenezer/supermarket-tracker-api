package com.supermarkettracker.infrastructure.persistence.adapter;
import com.supermarkettracker.domain.model.Cliente;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.ClienteRepository;
import com.supermarkettracker.infrastructure.persistence.mapper.ClientePersistenceMapper;
import com.supermarkettracker.infrastructure.persistence.repository.ClienteJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class ClienteRepositoryJpaAdapter implements ClienteRepository {
    private final ClienteJpaRepository jpa;
    private final ClientePersistenceMapper mapper;

    public ClienteRepositoryJpaAdapter(ClienteJpaRepository jpa, ClientePersistenceMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    public Cliente salvar(Cliente cliente) { return mapper.toDomain(jpa.save(mapper.toEntity(cliente))); }
    public Optional<Cliente> buscarPorId(Identificador id) { return jpa.findById(id.valor()).map(mapper::toDomain); }
    public List<Cliente> listarPorEmpresa(Identificador empresaId) {
        return jpa.findByEmpresaIdOrderByNomeAsc(empresaId.valor()).stream().map(mapper::toDomain).toList();
    }
}
