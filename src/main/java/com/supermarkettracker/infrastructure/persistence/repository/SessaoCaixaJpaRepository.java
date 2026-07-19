package com.supermarkettracker.infrastructure.persistence.repository;
import com.supermarkettracker.infrastructure.persistence.entity.SessaoCaixaEntity; import com.supermarkettracker.domain.model.enums.StatusSessaoCaixa; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface SessaoCaixaJpaRepository extends JpaRepository<SessaoCaixaEntity, UUID> { Optional<SessaoCaixaEntity> findByCaixaIdAndStatus(UUID caixaId, StatusSessaoCaixa status); }
