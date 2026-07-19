package com.supermarkettracker.infrastructure.persistence.converter;
import com.supermarkettracker.domain.model.valueobject.*;
import org.mapstruct.Named;
import java.math.BigDecimal; import java.util.UUID;

public class DomainValueConverter {
 public UUID idToUuid(Identificador value) { return value == null ? null : value.valor(); }
 public Identificador uuidToId(UUID value) { return value == null ? null : new Identificador(value); }
 public BigDecimal moneyToDecimal(Dinheiro value) { return value == null ? null : value.valor(); }
 public Dinheiro decimalToMoney(BigDecimal value) { return value == null ? null : new Dinheiro(value); }
 public BigDecimal quantityToDecimal(Quantidade value) { return value == null ? null : value.valor(); }
 public Quantidade decimalToQuantity(BigDecimal value) { return value == null ? null : new Quantidade(value); }
 public String documentToString(Documento value) { return value == null ? null : value.valor(); }
 public Documento stringToDocument(String value) { return value == null ? null : new Documento(value); }
 public String emailToString(Email value) { return value == null ? null : value.valor(); }
 public Email stringToEmail(String value) { return value == null ? null : new Email(value); }
}
