package com.supermarkettracker.infrastructure.persistence.repository;

import com.supermarkettracker.infrastructure.persistence.entity.ContaBancariaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContaBancariaJpaRepository extends JpaRepository<ContaBancariaEntity, UUID> {
    List<ContaBancariaEntity> findByEmpresaIdOrderByPrincipalDescBancoNomeAsc(UUID empresaId);
}
