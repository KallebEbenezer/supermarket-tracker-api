package com.supermarkettracker.infrastructure.persistence.repository;
import com.supermarkettracker.infrastructure.persistence.entity.*; import org.springframework.data.jpa.repository.*; import java.util.*;
public interface CaixaJpaRepository extends JpaRepository<CaixaEntity, UUID> {}
