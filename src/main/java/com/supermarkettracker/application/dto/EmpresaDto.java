package com.supermarkettracker.application.dto;
import java.util.UUID;
public record EmpresaDto(UUID id, String razaoSocial, String nomeFantasia, String cnpj, String status) { }
