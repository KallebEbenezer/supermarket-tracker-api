package com.supermarkettracker.infrastructure.persistence.repository;

import com.supermarkettracker.infrastructure.persistence.entity.CategoriaProdutoEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaProdutoJpaRepository extends JpaRepository<CategoriaProdutoEntity, UUID> {
    List<CategoriaProdutoEntity> findByEmpresaIdAndStatusOrderByNomeAsc(UUID empresaId,
            com.supermarkettracker.domain.model.enums.StatusAtivo status);
}
