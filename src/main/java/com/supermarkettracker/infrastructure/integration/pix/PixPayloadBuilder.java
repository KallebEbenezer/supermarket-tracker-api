package com.supermarkettracker.infrastructure.integration.pix;

import java.math.BigDecimal;

/**
 * Construtor de payload PIX no formato EMV (BR Code).
 * Gera copia-e-cola válido seguindo o padrão do Banco Central.
 */
public final class PixPayloadBuilder {

    private PixPayloadBuilder() {}

    /**
     * Monta o payload EMV completo para cobrança PIX.
     *
     * @param pixKey        Chave PIX (CPF, CNPJ, e-mail, telefone ou aleatória)
     * @param merchantName  Nome do recebedor (até 25 caracteres)
     * @param merchantCity  Cidade do recebedor (até 15 caracteres)
     * @param amount        Valor da transação (null para cobrança sem valor fixo)
     * @param txid          Identificador da transação (até 25 caracteres)
     * @return payload EMV pronto para uso como copia-e-cola e QR Code
     */
    public static String build(String pixKey, String merchantName, String merchantCity,
                               BigDecimal amount, String txid) {
        StringBuilder sb = new StringBuilder();

        // ID 00 - Payload Format Indicator
        appendField(sb, "00", "01");

        // ID 01 - Point of Initiation Method.
        // Sempre 11 (estático) aqui: esta classe monta cobranças locais sem URL de PSP.
        // Método 12 (dinâmico) só é válido quando o campo 26/subcampo 25 (url) aponta
        // para o PSP — caso contrário o banco rejeita o QR como inválido.
        appendField(sb, "01", "11");

        // ID 26 - Merchant Account Information (GUI)
        StringBuilder merchantInfo = new StringBuilder();
        appendField(merchantInfo, "00", "br.gov.bcb.pix");  // GUI
        appendField(merchantInfo, "01", pixKey);             // Chave PIX
        appendField(sb, "26", merchantInfo.toString());

        // ID 53 - Transaction Currency (986 = BRL)
        appendField(sb, "53", "986");

        // ID 54 - Transaction Amount
        if (amount != null) {
            appendField(sb, "54", amount.toPlainString());
        }

        // ID 58 - Country Code
        appendField(sb, "58", "BR");

        // ID 59 - Merchant Name (máx 25)
        appendField(sb, "59", truncate(merchantName, 25));

        // ID 60 - Merchant City (máx 15)
        appendField(sb, "60", truncate(merchantCity, 15));

        // ID 62 - Additional Data Field Template
        if (txid != null && !txid.isEmpty()) {
            StringBuilder additionalData = new StringBuilder();
            appendField(additionalData, "05", truncate(txid, 25));
            appendField(sb, "62", additionalData.toString());
        }

        // ID 63 - CRC16 (placeholder, calculado abaixo)
        String payload = sb.toString() + "6304";
        String crc = calculateCRC16(payload);
        return payload + crc;
    }

    private static void appendField(StringBuilder sb, String id, String value) {
        sb.append(id);
        sb.append(String.format("%02d", value.length()));
        sb.append(value);
    }

    private static String truncate(String value, int maxLength) {
        if (value == null) return "";
        return value.length() > maxLength ? value.substring(0, maxLength) : value;
    }

    /**
     * Calcula CRC16-CCITT do payload PIX (polinômio 0x1021).
     */
    static String calculateCRC16(String payload) {
        int polinomio = 0x1021;
        int resultado = 0xFFFF;

        byte[] bytes = payload.getBytes();
        for (byte b : bytes) {
            resultado ^= (b & 0xFF) << 8;
            for (int i = 0; i < 8; i++) {
                if ((resultado & 0x8000) != 0) {
                    resultado = (resultado << 1) ^ polinomio;
                } else {
                    resultado <<= 1;
                }
                resultado &= 0xFFFF;
            }
        }

        return String.format("%04X", resultado);
    }
}
