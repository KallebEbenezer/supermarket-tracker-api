package com.supermarkettracker.infrastructure.persistence.repository;

import com.supermarkettracker.infrastructure.persistence.entity.SumupOAuthStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SumupOAuthStateJpaRepository extends JpaRepository<SumupOAuthStateEntity, String> { }
