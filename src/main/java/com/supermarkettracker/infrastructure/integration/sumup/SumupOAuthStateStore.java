package com.supermarkettracker.infrastructure.integration.sumup;

import com.supermarkettracker.domain.exception.GatewayIndisponivelException;
import com.supermarkettracker.infrastructure.persistence.entity.SumupOAuthStateEntity;
import com.supermarkettracker.infrastructure.persistence.repository.SumupOAuthStateJpaRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SumupOAuthStateStore {
    private final SumupOAuthStateJpaRepository repository;

    public SumupOAuthStateStore(SumupOAuthStateJpaRepository repository) { this.repository = repository; }

    @Transactional
    public void save(String state) {
        var entity = new SumupOAuthStateEntity();
        entity.stateHash = hash(state);
        entity.expiraEm = Instant.now().plus(Duration.ofMinutes(10));
        try {
            // Força a gravação dentro desta transação para que uma migration
            // ausente ou indisponibilidade do banco não vire um 500 genérico
            // depois que o controller já retornou.
            repository.saveAndFlush(entity);
        } catch (DataAccessException exception) {
            throw new GatewayIndisponivelException(
                    "Não foi possível armazenar a autorização OAuth da SumUp. Verifique a migration do banco.",
                    exception);
        }
    }

    @Transactional
    public boolean consume(String state) {
        String stateHash = hash(state);
        var entity = repository.findById(stateHash).orElse(null);
        if (entity == null) return false;
        repository.delete(entity);
        return entity.expiraEm.isAfter(Instant.now());
    }

    private String hash(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("SHA-256 indisponível", exception);
        }
    }
}
