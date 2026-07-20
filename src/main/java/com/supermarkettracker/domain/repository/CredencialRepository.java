package com.supermarkettracker.domain.repository;

import com.supermarkettracker.domain.model.Credencial;
import com.supermarkettracker.domain.model.valueobject.Email;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import java.util.Optional;

public interface CredencialRepository {
    Credencial salvar(Credencial credencial);
    Optional<Credencial> buscarPorEmail(Email email);
    Optional<Credencial> buscarPorUsuarioId(Identificador usuarioId);
    Optional<Credencial> buscarPorTokenReset(String token);
}
