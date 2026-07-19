package com.supermarkettracker.domain.gateway;

import com.supermarkettracker.domain.model.valueobject.Identificador;

public interface ArmazenamentoImagemGateway { String armazenarImagemProduto(Identificador empresaId, String nomeArquivo, byte[] conteudo); void removerImagem(String url); }
