package com.desafio.estagio.validation.internal;

import org.apache.wicket.validation.Validatable;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CNPJValidatorTest {

    private CNPJValidator validator;

    @Before
    public void setUp() {
        validator = new CNPJValidator();
    }

    @Test
    public void shouldAcceptNullInput() {
        Validatable<String> validatable = new Validatable<>(null);
        validator.validate(validatable);
        assertThat(validatable.getErrors()).isEmpty();
    }

    @Test
    public void shouldAcceptBlankInput() {
        Validatable<String> validatable = new Validatable<>("   ");
        validator.validate(validatable);
        assertThat(validatable.getErrors()).isEmpty();
    }

    @Test
    public void shouldAcceptEmptyInput() {
        Validatable<String> validatable = new Validatable<>("");
        validator.validate(validatable);
        assertThat(validatable.getErrors()).isEmpty();
    }

    @Test
    public void shouldAcceptValidCNPJRawDigits() {
        Validatable<String> validatable = new Validatable<>("11444777000161");
        validator.validate(validatable);
        assertThat(validatable.getErrors()).isEmpty();
    }

    @Test
    public void shouldAcceptValidCNPJFormatted() {
        Validatable<String> validatable = new Validatable<>("11.444.777/0001-61");
        validator.validate(validatable);
        assertThat(validatable.getErrors()).isEmpty();
    }

    @Test
    public void shouldAcceptAnotherValidCNPJ() {
        Validatable<String> validatable = new Validatable<>("45316333000162");
        validator.validate(validatable);
        assertThat(validatable.getErrors()).isEmpty();
    }

    @Test
    public void shouldRejectCNPJWithWrongCheckDigit() {
        Validatable<String> validatable = new Validatable<>("11444777000162");
        validator.validate(validatable);
        assertThat(validatable.getErrors()).isNotEmpty();
    }

    @Test
    public void shouldRejectCNPJWithAllSameDigits() {
        Validatable<String> validatable = new Validatable<>("11111111111111");
        validator.validate(validatable);
        assertThat(validatable.getErrors()).isNotEmpty();
    }

    @Test
    public void shouldRejectCNPJWithAllZeros() {
        Validatable<String> validatable = new Validatable<>("00000000000000");
        validator.validate(validatable);
        assertThat(validatable.getErrors()).isNotEmpty();
    }

    @Test
    public void shouldRejectCNPJWithOnly13Digits() {
        Validatable<String> validatable = new Validatable<>("1234567890123");
        validator.validate(validatable);
        assertThat(validatable.getErrors()).isNotEmpty();
    }

    @Test
    public void shouldRejectCNPJWithOnly15Digits() {
        Validatable<String> validatable = new Validatable<>("123456789012345");
        validator.validate(validatable);
        assertThat(validatable.getErrors()).isNotEmpty();
    }

    @Test
    public void shouldRejectCNPJWithLetters() {
        Validatable<String> validatable = new Validatable<>("ab114447770001");
        validator.validate(validatable);
        assertThat(validatable.getErrors()).isNotEmpty();
    }

    @Test
    public void shouldRejectCNPJWithSpecialChars() {
        Validatable<String> validatable = new Validatable<>("@@.@@@.@@@/@@@@-@@");
        validator.validate(validatable);
        assertThat(validatable.getErrors()).isNotEmpty();
    }
}
