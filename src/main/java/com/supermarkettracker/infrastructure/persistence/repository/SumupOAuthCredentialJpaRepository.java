package com.supermarkettracker.infrastructure.persistence.repository;

import com.supermarkettracker.infrastructure.persistence.entity.SumupOAuthCredentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SumupOAuthCredentialJpaRepository extends JpaRepository<SumupOAuthCredentialEntity, Short> { }
