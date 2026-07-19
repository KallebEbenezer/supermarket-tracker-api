package com.supermarkettracker.infrastructure.persistence.repository;
import com.supermarkettracker.infrastructure.persistence.entity.UsuarioEntity; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, UUID> { Optional<UsuarioEntity> findByEmail(String email); }
