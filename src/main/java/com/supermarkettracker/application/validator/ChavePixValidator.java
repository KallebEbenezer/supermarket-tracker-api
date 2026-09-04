package com.supermarkettracker.application.validator;

import com.supermarkettracker.domain.exception.RegraDeDominioException;
import java.util.regex.Pattern;

/**
 * Valida e normaliza chaves PIX nos formatos aceitos pelo BACEN:
 * CPF, CNPJ, e-mail, telefone (com +55) e chave aleatória (UUID/EVP).
 *
 * <p>Uma chave malformada embutida no BR Code faz o banco rejeitar o QR como
 * "chave PIX inválida", por isso a validação acontece no salvamento da conta.
 */
public final class ChavePixValidator {

    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern UUID =
            Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    private ChavePixValidator() { }

    /**
     * Normaliza e valida uma chave PIX.
     *
     * @param chavePix chave bruta informada pelo usuário
     * @return a chave normalizada, pronta para uso no BR Code
     * @throws RegraDeDominioException se a chave não for um formato PIX válido
     */
    public static String validar(String chavePix) {
        if (chavePix == null || chavePix.isBlank()) {
            throw new RegraDeDominioException("Chave PIX e obrigatoria");
        }
        String chave = chavePix.trim();

        // E-mail
        if (chave.contains("@")) {
            if (!EMAIL.matcher(chave).matches()) {
                throw new RegraDeDominioException("Chave PIX de e-mail invalida: " + chavePix);
            }
            return chave.toLowerCase();
        }

        // Chave aleatória (EVP) — UUID
        if (UUID.matcher(chave).matches()) {
            return chave.toLowerCase();
        }

        // Telefone: deve vir com indicativo de país (+55)
        if (chave.startsWith("+") || chave.startsWith("55")) {
            return validarTelefone(chave, chavePix);
        }

        String apenasDigitos = chave.replaceAll("\\D", "");

        // CPF (11 dígitos) ou CNPJ (14 dígitos)
        if (apenasDigitos.length() == 11) {
            validarCpf(apenasDigitos, chavePix);
            return apenasDigitos;
        }
        if (apenasDigitos.length() == 14) {
            validarCnpj(apenasDigitos, chavePix);
            return apenasDigitos;
        }

        throw new RegraDeDominioException("Chave PIX invalida: " + chavePix);
    }

    private static String validarTelefone(String chave, String original) {
        String digitos = chave.replaceAll("\\D", "");
        if (digitos.length() < 12 || digitos.length() > 13) {
            throw new RegraDeDominioException("Chave PIX de telefone invalida: " + original);
        }
        // Remove o código do país (55) e normaliza com +55
        String numero = digitos.length() == 13 ? digitos.substring(2) : digitos.substring(2);
        return "+55" + numero;
    }

    private static void validarCpf(String cpf, String original) {
        if (!isCpfValido(cpf)) {
            throw new RegraDeDominioException("Chave PIX (CPF) invalida: " + original);
        }
    }

    private static void validarCnpj(String cnpj, String original) {
        if (!isCnpjValido(cnpj)) {
            throw new RegraDeDominioException("Chave PIX (CNPJ) invalida: " + original);
        }
    }

    static boolean isCpfValido(String cpf) {
        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
            return false;
        }
        return digitoVerificador(cpf, 9) == cpf.charAt(9) - '0'
                && digitoVerificador(cpf, 10) == cpf.charAt(10) - '0';
    }

    private static int digitoVerificador(String cpf, int posicao) {
        int soma = 0;
        int peso = posicao + 1; // 1º dígito: pesos 10..2; 2º dígito: pesos 11..2
        for (int i = 0; i < posicao; i++) {
            soma += (cpf.charAt(i) - '0') * peso--;
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    static boolean isCnpjValido(String cnpj) {
        if (cnpj.length() != 14 || cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }
        int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int d1 = calcularDigitoCnpj(cnpj, pesos1);
        int d2 = calcularDigitoCnpj(cnpj, pesos2);
        return d1 == cnpj.charAt(12) - '0' && d2 == cnpj.charAt(13) - '0';
    }

    private static int calcularDigitoCnpj(String cnpj, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < pesos.length; i++) {
            soma += (cnpj.charAt(i) - '0') * pesos[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
