package com.desafio.estagio.validation;

public final class ValidationConstants {

    private ValidationConstants() {}

    // --- Cliente Fisico ---
    public static final int NOME_MIN = 3;
    public static final int NOME_MAX = 150;

    public static final int RG_LENGTH_MIN = 7;
    public static final int RG_LENGTH_MAX = 9;

    public static final int CPF_LENGTH_FORMATTED_MIN = 11;
    public static final int CPF_LENGTH_FORMATTED_MAX = 14;

    // --- Cliente Juridico ---
    public static final int RAZAO_SOCIAL_MIN = 3;
    public static final int RAZAO_SOCIAL_MAX = 150;

    public static final int CNPJ_LENGTH_FORMATTED_MIN = 14;
    public static final int CNPJ_LENGTH_FORMATTED_MAX = 18;

    // --- Address ---
    public static final int ESTADO_LENGTH = 2;

    // --- Email ---
    public static final int EMAIL_MAX = 150;

    // --- Endereco fields ---
    public static final int LOGRADOURO_MIN = 3;
    public static final int LOGRADOURO_MAX = 150;
    public static final int BAIRRO_MIN = 3;
    public static final int BAIRRO_MAX = 100;
    public static final int CIDADE_MIN = 3;
    public static final int CIDADE_MAX = 100;
    public static final int CEP_MAX = 9;
    public static final int TELEFONE_MAX = 16;
    public static final int COMPLEMENTO_MAX = 150;

    // --- Cliente Juridico ---
    public static final int INSCRICAO_ESTADUAL_MAX = 20;
}
