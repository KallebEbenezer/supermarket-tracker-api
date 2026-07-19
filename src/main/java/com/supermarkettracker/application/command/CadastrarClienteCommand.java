package com.supermarkettracker.application.command;
import java.time.LocalDate;
import java.util.UUID;
public record CadastrarClienteCommand(UUID empresaId, String nome, String cpfCnpj, String email, String telefone,
                                      LocalDate dataNascimento) { }
