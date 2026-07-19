package com.supermarkettracker.application.command;
import java.util.UUID;
public record CadastrarUsuarioCommand(UUID authUserId, String nome, String email, String telefone) { }
