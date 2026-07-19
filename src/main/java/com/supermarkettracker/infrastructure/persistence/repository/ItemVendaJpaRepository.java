package com.supermarkettracker.infrastructure.persistence.repository;
import com.supermarkettracker.infrastructure.persistence.entity.ItemVendaEntity; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface ItemVendaJpaRepository extends JpaRepository<ItemVendaEntity, UUID> { List<ItemVendaEntity> findByVendaIdOrderByNumeroAsc(UUID vendaId); }
