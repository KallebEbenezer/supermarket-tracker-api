package com.supermarkettracker.application.command;
import java.util.UUID;
public record CadastrarLojaCommand(UUID empresaId, String codigo, String nome, String cnpj, String telefone) { }
