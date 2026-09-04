package com.supermarkettracker.infrastructure.persistence.repository;

import com.supermarkettracker.infrastructure.persistence.entity.PagamentoPixEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PagamentoPixJpaRepository extends JpaRepository<PagamentoPixEntity, UUID> {
    Optional<PagamentoPixEntity> findByGatewayTransacaoId(String gatewayTransacaoId);

    @Query(value = "SELECT pp.* FROM pagamento_pix pp JOIN pagamento p ON pp.pagamento_id = p.id WHERE p.venda_id = :vendaId LIMIT 1", nativeQuery = true)
    Optional<PagamentoPixEntity> findByVendaId(@Param("vendaId") UUID vendaId);
}
