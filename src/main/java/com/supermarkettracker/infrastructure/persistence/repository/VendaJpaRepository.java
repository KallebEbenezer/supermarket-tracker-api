package com.supermarkettracker.infrastructure.persistence.repository;
import com.supermarkettracker.infrastructure.persistence.entity.*; import org.springframework.data.domain.*; import org.springframework.data.jpa.repository.*; import java.util.*;
public interface VendaJpaRepository extends JpaRepository<VendaEntity, UUID>, JpaSpecificationExecutor<VendaEntity> {
    List<VendaEntity> findByEmpresaIdOrderByCriadoEmDesc(UUID empresaId);
    List<VendaEntity> findByEmpresaIdAndLojaIdOrderByCriadoEmDesc(UUID empresaId, UUID lojaId);
}
