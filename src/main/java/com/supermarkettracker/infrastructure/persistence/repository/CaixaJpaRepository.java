package com.supermarkettracker.infrastructure.persistence.repository;
import com.supermarkettracker.infrastructure.persistence.entity.*; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param; import java.util.*;
public interface CaixaJpaRepository extends JpaRepository<CaixaEntity, UUID> {
    List<CaixaEntity> findByLojaIdOrderByNomeAsc(UUID lojaId);
    @Query("SELECT c FROM CaixaEntity c JOIN LojaEntity l ON c.lojaId = l.id WHERE l.empresaId = :empresaId ORDER BY c.nome ASC")
    List<CaixaEntity> findByEmpresaId(@Param("empresaId") UUID empresaId);
}
