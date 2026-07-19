package com.supermarkettracker.infrastructure.persistence.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supermarkettracker.domain.model.valueobject.Endereco;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** Serializa o value object de endereço exatamente na coluna JSONB da migration. */
@Converter
public class EnderecoJsonConverter implements AttributeConverter<Endereco, String> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Endereco endereco) {
        if (endereco == null) return null;
        try {
            return OBJECT_MAPPER.writeValueAsString(endereco);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Não foi possível serializar o endereço", exception);
        }
    }

    @Override
    public Endereco convertToEntityAttribute(String json) {
        if (json == null || json.isBlank()) return null;
        try {
            return OBJECT_MAPPER.readValue(json, Endereco.class);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Não foi possível desserializar o endereço", exception);
        }
    }
}
