package com.supermarkettracker.domain.gateway;

import com.supermarkettracker.domain.model.valueobject.Identificador;

/** Porta para armazenamento de objetos, independente do fornecedor. */
public interface StorageGateway {
    String armazenar(Identificador empresaId, String chave, String contentType, byte[] conteudo);
    void remover(String url);
}
