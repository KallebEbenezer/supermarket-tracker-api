package com.supermarkettracker.infrastructure.persistence.repository;

import com.supermarkettracker.infrastructure.persistence.entity.PagamentoPixEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagamentoPixJpaRepository extends JpaRepository<PagamentoPixEntity, UUID> { }
