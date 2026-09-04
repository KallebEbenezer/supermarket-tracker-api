package com.supermarkettracker.infrastructure.persistence.adapter;

import com.supermarkettracker.domain.model.Credencial;
import com.supermarkettracker.domain.model.valueobject.Email;
import com.supermarkettracker.domain.repository.CredencialRepository;
import com.supermarkettracker.infrastructure.persistence.mapper.CredencialPersistenceMapper;
import com.supermarkettracker.infrastructure.persistence.repository.CredencialJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import com.supermarkettracker.domain.model.valueobject.Identificador;

@Repository
public class CredencialRepositoryJpaAdapter implements CredencialRepository {
    private final CredencialJpaRepository jpa;
    private final CredencialPersistenceMapper mapper;

    public CredencialRepositoryJpaAdapter(CredencialJpaRepository jpa, CredencialPersistenceMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Credencial salvar(Credencial credencial) {
        return mapper.toDomain(jpa.save(mapper.toEntity(credencial)));
    }

    @Override
    public Optional<Credencial> buscarPorEmail(Email email) {
        return jpa.findByEmail(email.valor()).map(mapper::toDomain);
    }

    @Override
    public Optional<Credencial> buscarPorUsuarioId(Identificador usuarioId) {
        return jpa.findByUsuarioId(usuarioId.valor()).map(mapper::toDomain);
    }

    @Override
    public Optional<Credencial> buscarPorTokenReset(String token) {
        return jpa.findByTokenResetSenha(token).map(mapper::toDomain);
    }
}
