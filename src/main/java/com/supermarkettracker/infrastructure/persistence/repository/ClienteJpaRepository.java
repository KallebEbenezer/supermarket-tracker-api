package com.supermarkettracker.infrastructure.persistence.repository;
import com.supermarkettracker.infrastructure.persistence.entity.ClienteEntity; import org.springframework.data.jpa.repository.JpaRepository; import java.util.UUID;
public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, UUID> {}
