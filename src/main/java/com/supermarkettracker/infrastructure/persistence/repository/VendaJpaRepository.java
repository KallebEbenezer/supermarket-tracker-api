package com.supermarkettracker.infrastructure.persistence.repository;
import com.supermarkettracker.infrastructure.persistence.entity.*; import org.springframework.data.domain.*; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param; import java.util.*;
public interface VendaJpaRepository extends JpaRepository<VendaEntity, UUID>, JpaSpecificationExecutor<VendaEntity> {
    List<VendaEntity> findByEmpresaIdOrderByCriadoEmDesc(UUID empresaId);
    List<VendaEntity> findByEmpresaIdAndLojaIdOrderByCriadoEmDesc(UUID empresaId, UUID lojaId);
    @Query("SELECT MAX(v.numero) FROM VendaEntity v WHERE v.empresaId = :empresaId")
    Optional<Long> findMaxNumeroByEmpresaId(@Param("empresaId") UUID empresaId);
}
