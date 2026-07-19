package com.supermarkettracker.infrastructure.integration.mock;

import com.supermarkettracker.domain.gateway.StorageGateway;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import org.springframework.stereotype.Component;

@Component
public class StorageMockAdapter implements StorageGateway {
    @Override
    public String armazenar(Identificador empresaId, String chave, String contentType, byte[] conteudo) {
        return "mock://storage/" + empresaId.valor() + "/" + chave;
    }

    @Override
    public void remover(String url) {
        // Mock sem estado: a remoção é propositalmente uma operação sem efeito.
    }
}
