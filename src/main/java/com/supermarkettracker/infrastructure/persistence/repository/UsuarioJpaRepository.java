package com.supermarkettracker.infrastructure.persistence.repository;
import com.supermarkettracker.infrastructure.persistence.entity.UsuarioEntity; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, UUID> {
    Optional<UsuarioEntity> findByEmail(String email);
    @org.springframework.data.jpa.repository.Query("select u from UsuarioEntity u join EmpresaUsuarioEntity eu on eu.usuarioId = u.id where eu.empresaId = :empresaId")
    List<UsuarioEntity> findByEmpresaId(UUID empresaId);
}
