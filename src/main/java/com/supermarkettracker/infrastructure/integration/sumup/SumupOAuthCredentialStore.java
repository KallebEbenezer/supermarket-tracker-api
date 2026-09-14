package com.supermarkettracker.infrastructure.integration.sumup;

import com.supermarkettracker.infrastructure.config.JwtProperties;
import com.supermarkettracker.infrastructure.persistence.entity.SumupOAuthCredentialEntity;
import com.supermarkettracker.infrastructure.persistence.repository.SumupOAuthCredentialJpaRepository;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Armazena o refresh token cifrado no banco, usando uma chave derivada do segredo JWT do servidor. */
@Service
public class SumupOAuthCredentialStore {
    private static final short SINGLETON_ID = 1;
    private static final int IV_LENGTH = 12;
    private final SumupOAuthCredentialJpaRepository repository;
    private final SecretKeySpec encryptionKey;
    private final SecureRandom random = new SecureRandom();

    public SumupOAuthCredentialStore(SumupOAuthCredentialJpaRepository repository, JwtProperties jwtProperties) {
        this.repository = repository;
        this.encryptionKey = new SecretKeySpec(sha256(jwtProperties.secret() + ":sumup-oauth-refresh:v1"), "AES");
    }

    @Transactional(readOnly = true)
    public Optional<String> refreshToken() {
        return repository.findById(SINGLETON_ID).map(entity -> decrypt(entity.refreshTokenCriptografado));
    }

    @Transactional
    public void save(String refreshToken) {
        var entity = repository.findById(SINGLETON_ID).orElseGet(SumupOAuthCredentialEntity::new);
        entity.id = SINGLETON_ID;
        entity.refreshTokenCriptografado = encrypt(refreshToken);
        entity.atualizadoEm = Instant.now();
        repository.save(entity);
    }

    private String encrypt(String value) {
        try {
            byte[] iv = new byte[IV_LENGTH];
            random.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, encryptionKey, new GCMParameterSpec(128, iv));
            byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(ByteBuffer.allocate(iv.length + encrypted.length)
                    .put(iv).put(encrypted).array());
        } catch (Exception exception) {
            throw new IllegalStateException("Não foi possível proteger a credencial OAuth da SumUp", exception);
        }
    }

    private String decrypt(String value) {
        try {
            byte[] payload = Base64.getDecoder().decode(value);
            byte[] iv = new byte[IV_LENGTH];
            byte[] encrypted = new byte[payload.length - IV_LENGTH];
            System.arraycopy(payload, 0, iv, 0, IV_LENGTH);
            System.arraycopy(payload, IV_LENGTH, encrypted, 0, encrypted.length);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, encryptionKey, new GCMParameterSpec(128, iv));
            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (Exception exception) {
            throw new IllegalStateException("Não foi possível ler a credencial OAuth da SumUp", exception);
        }
    }

    private static byte[] sha256(String value) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (Exception exception) {
            throw new IllegalStateException("SHA-256 indisponível", exception);
        }
    }
}
