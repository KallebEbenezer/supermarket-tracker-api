package com.supermarkettracker.infrastructure.persistence.repository;
import com.supermarkettracker.infrastructure.persistence.entity.LojaEntity; import org.springframework.data.domain.*; import org.springframework.data.jpa.repository.*; import java.util.*;
public interface LojaJpaRepository extends JpaRepository<LojaEntity, UUID>, JpaSpecificationExecutor<LojaEntity> { List<LojaEntity> findByEmpresaIdOrderByNomeAsc(UUID empresaId); Page<LojaEntity> findByEmpresaIdAndStatus(UUID empresaId, com.supermarkettracker.domain.model.enums.StatusAtivo status, Pageable pageable); }
