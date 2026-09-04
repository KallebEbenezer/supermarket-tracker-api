package com.supermarkettracker.infrastructure.persistence.adapter;
import com.supermarkettracker.domain.model.EmpresaUsuario;
import com.supermarkettracker.domain.model.enums.StatusAtivo;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.domain.repository.EmpresaUsuarioRepository;
import com.supermarkettracker.infrastructure.persistence.mapper.EmpresaUsuarioPersistenceMapper;
import com.supermarkettracker.infrastructure.persistence.repository.EmpresaUsuarioJpaRepository;
import java.util.List;
import org.springframework.stereotype.Repository;
@Repository
public class EmpresaUsuarioRepositoryJpaAdapter implements EmpresaUsuarioRepository {
    private final EmpresaUsuarioJpaRepository jpa;
    private final EmpresaUsuarioPersistenceMapper mapper;
    public EmpresaUsuarioRepositoryJpaAdapter(EmpresaUsuarioJpaRepository jpa, EmpresaUsuarioPersistenceMapper mapper) { this.jpa = jpa; this.mapper = mapper; }
    public EmpresaUsuario salvar(EmpresaUsuario eu) { return mapper.toDomain(jpa.save(mapper.toEntity(eu))); }
    public List<EmpresaUsuario> buscarPorUsuario(Identificador usuarioId) {
        return jpa.findByUsuarioIdAndStatusOrderByCriadoEmDesc(usuarioId.valor(), StatusAtivo.ATIVO)
                .stream().map(mapper::toDomain).toList();
    }
}
