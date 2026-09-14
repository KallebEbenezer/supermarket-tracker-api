package com.supermarkettracker.infrastructure.integration.sumup;

import com.supermarkettracker.infrastructure.persistence.entity.SumupOAuthStateEntity;
import com.supermarkettracker.infrastructure.persistence.repository.SumupOAuthStateJpaRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import org.springframework.stereotype.Service;
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
        repository.save(entity);
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
