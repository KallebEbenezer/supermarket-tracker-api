package com.supermarkettracker.infrastructure.persistence.repository;

import com.supermarkettracker.infrastructure.persistence.entity.PagamentoCartaoEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagamentoCartaoJpaRepository extends JpaRepository<PagamentoCartaoEntity, UUID> { }
