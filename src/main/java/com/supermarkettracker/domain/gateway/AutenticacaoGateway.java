package com.supermarkettracker.domain.gateway;

import com.supermarkettracker.domain.model.valueobject.Email;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import java.util.Optional;

public interface AutenticacaoGateway { Optional<Identificador> buscarUsuarioPorEmail(Email email); }
