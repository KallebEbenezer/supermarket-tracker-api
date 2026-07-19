package com.supermarkettracker.infrastructure.integration.mock;

import com.supermarkettracker.domain.gateway.ArmazenamentoImagemGateway;
import com.supermarkettracker.domain.gateway.StorageGateway;
import com.supermarkettracker.domain.model.valueobject.Identificador;
import org.springframework.stereotype.Component;

/** Adapter de compatibilidade para o contrato de imagens já presente no domínio. */
@Component
public class ArmazenamentoImagemMockAdapter implements ArmazenamentoImagemGateway {
    private final StorageGateway storageGateway;

    public ArmazenamentoImagemMockAdapter(StorageGateway storageGateway) {
        this.storageGateway = storageGateway;
    }

    @Override
    public String armazenarImagemProduto(Identificador empresaId, String nomeArquivo, byte[] conteudo) {
        return storageGateway.armazenar(empresaId, "produtos/" + nomeArquivo, "application/octet-stream", conteudo);
    }

    @Override
    public void removerImagem(String url) {
        storageGateway.remover(url);
    }
}
