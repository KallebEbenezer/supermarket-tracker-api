package com.supermarkettracker.infrastructure.persistence.repository;

import com.supermarkettracker.infrastructure.persistence.entity.CredencialEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CredencialJpaRepository extends JpaRepository<CredencialEntity, UUID> {
    Optional<CredencialEntity> findByEmail(String email);
    Optional<CredencialEntity> findByUsuarioId(UUID usuarioId);
    Optional<CredencialEntity> findByTokenResetSenha(String token);
}
