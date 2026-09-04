package com.supermarkettracker.application.dto;
import java.util.UUID;
public record CaixaDto(UUID id, UUID lojaId, String codigo, String nome, String status) { }
