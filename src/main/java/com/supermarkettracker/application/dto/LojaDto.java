package com.supermarkettracker.application.dto;
import java.util.UUID;
public record LojaDto(UUID id, UUID empresaId, String codigo, String nome, String status) { }
