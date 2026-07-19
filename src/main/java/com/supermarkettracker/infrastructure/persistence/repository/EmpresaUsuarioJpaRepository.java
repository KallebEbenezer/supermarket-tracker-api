package com.supermarkettracker.infrastructure.persistence.repository;

import com.supermarkettracker.infrastructure.persistence.entity.EmpresaUsuarioEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaUsuarioJpaRepository extends JpaRepository<EmpresaUsuarioEntity, UUID> {
    Optional<EmpresaUsuarioEntity> findByEmpresaIdAndUsuarioId(UUID empresaId, UUID usuarioId);
}
