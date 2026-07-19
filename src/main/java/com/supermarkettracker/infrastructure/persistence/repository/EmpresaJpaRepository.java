package com.supermarkettracker.infrastructure.persistence.repository;
import com.supermarkettracker.infrastructure.persistence.entity.EmpresaEntity; import org.springframework.data.jpa.repository.JpaRepository; import java.util.UUID;
public interface EmpresaJpaRepository extends JpaRepository<EmpresaEntity, UUID> {}
