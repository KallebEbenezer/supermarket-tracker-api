package com.supermarkettracker.infrastructure.persistence.mapper;

import com.supermarkettracker.domain.model.Credencial;
import com.supermarkettracker.domain.model.valueobject.Email;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import com.supermarkettracker.infrastructure.persistence.entity.CredencialEntity;

/** Converte entre {@link Credencial} (domínio) e {@link CredencialEntity} (JPA). */
public final class CredencialPersistenceMapper {
    private CredencialPersistenceMapper() { }

    public CredencialEntity toEntity(Credencial c) {
        var e = new CredencialEntity();
        e.id = c.id().valor();
        e.email = c.email().valor();
        e.senhaHash = c.senhaHash();
        e.usuarioId = c.usuarioId().valor();
        e.papel = c.papel();
        e.ativo = c.ativo();
        e.tokenResetSenha = c.tokenResetSenha();
        e.expiraResetEm = c.expiraResetEm();
        e.criadoEm = c.criadoEm();
        e.atualizadoEm = c.atualizadoEm();
        return e;
    }

    public Credencial toDomain(CredencialEntity e) {
        return new Credencial(
                new Identificador(e.id),
                new Email(e.email),
                e.senhaHash,
                new Identificador(e.usuarioId),
                e.papel,
                e.ativo,
                e.tokenResetSenha,
                e.expiraResetEm,
                e.criadoEm,
                e.atualizadoEm);
    }
}
