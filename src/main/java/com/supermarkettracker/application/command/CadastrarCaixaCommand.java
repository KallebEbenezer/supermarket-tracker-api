package com.supermarkettracker.application.command;
import java.util.UUID;
public record CadastrarCaixaCommand(UUID lojaId, String codigo, String nome) { }
