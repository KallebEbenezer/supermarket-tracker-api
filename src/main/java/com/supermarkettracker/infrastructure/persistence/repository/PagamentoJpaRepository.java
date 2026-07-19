package com.supermarkettracker.infrastructure.persistence.repository;
import com.supermarkettracker.infrastructure.persistence.entity.PagamentoEntity; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface PagamentoJpaRepository extends JpaRepository<PagamentoEntity, UUID> { List<PagamentoEntity> findByVendaIdOrderByCriadoEmAsc(UUID vendaId); }
