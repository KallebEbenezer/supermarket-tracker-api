package com.supermarkettracker.infrastructure.persistence.repository;

import com.supermarkettracker.infrastructure.persistence.entity.DashboardCacheDiarioEntity;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardCacheDiarioJpaRepository extends JpaRepository<DashboardCacheDiarioEntity, UUID> {
    Optional<DashboardCacheDiarioEntity> findByEmpresaIdAndLojaIdAndDataReferencia(UUID empresaId, UUID lojaId,
            LocalDate dataReferencia);
}
