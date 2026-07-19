package com.supermarkettracker.domain.model.valueobject;

public record Endereco(String logradouro, String numero, String complemento, String bairro,
                       String cidade, String estado, String cep) { }
