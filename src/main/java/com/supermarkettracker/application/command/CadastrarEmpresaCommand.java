package com.supermarkettracker.application.command;
import java.util.UUID;
public record CadastrarEmpresaCommand(String razaoSocial, String nomeFantasia, String cnpj, UUID usuarioId) { }
