package com.supermarkettracker.infrastructure.persistence.repository;
import com.supermarkettracker.infrastructure.persistence.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, UUID> {
    List<ClienteEntity> findByEmpresaIdOrderByNomeAsc(UUID empresaId);
}
