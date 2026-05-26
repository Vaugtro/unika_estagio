package com.desafio.estagio.validation.internal;

import org.apache.wicket.validation.Validatable;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CPFValidatorTest {

    private CPFValidator validator;

    @Before
    public void setUp() {
        validator = new CPFValidator();
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
    public void shouldAcceptValidCPFRawDigits() {
        Validatable<String> validatable = new Validatable<>("52998224725");
        validator.validate(validatable);
        assertThat(validatable.getErrors()).isEmpty();
    }

    @Test
    public void shouldAcceptValidCPFFormatted() {
        Validatable<String> validatable = new Validatable<>("529.982.247-25");
        validator.validate(validatable);
        assertThat(validatable.getErrors()).isEmpty();
    }

    @Test
    public void shouldAcceptAnotherValidCPF() {
        Validatable<String> validatable = new Validatable<>("12345678909");
        validator.validate(validatable);
        assertThat(validatable.getErrors()).isEmpty();
    }

    @Test
    public void shouldRejectCPFWithWrongCheckDigit() {
        Validatable<String> validatable = new Validatable<>("52998224726");
        validator.validate(validatable);
        assertThat(validatable.getErrors()).isNotEmpty();
    }

    @Test
    public void shouldRejectCPFWithAllSameDigits() {
        Validatable<String> validatable = new Validatable<>("11111111111");
        validator.validate(validatable);
        assertThat(validatable.getErrors()).isNotEmpty();
    }

    @Test
    public void shouldRejectCPFWithAllZeros() {
        Validatable<String> validatable = new Validatable<>("00000000000");
        validator.validate(validatable);
        assertThat(validatable.getErrors()).isNotEmpty();
    }

    @Test
    public void shouldRejectCPFWithOnly10Digits() {
        Validatable<String> validatable = new Validatable<>("1234567890");
        validator.validate(validatable);
        assertThat(validatable.getErrors()).isNotEmpty();
    }

    @Test
    public void shouldRejectCPFWithOnly12Digits() {
        Validatable<String> validatable = new Validatable<>("123456789012");
        validator.validate(validatable);
        assertThat(validatable.getErrors()).isNotEmpty();
    }

    @Test
    public void shouldRejectCPFWithLetters() {
        Validatable<String> validatable = new Validatable<>("abc12345678");
        validator.validate(validatable);
        assertThat(validatable.getErrors()).isNotEmpty();
    }

    @Test
    public void shouldRejectCPFWithSpecialChars() {
        Validatable<String> validatable = new Validatable<>("@@@.@@@.@@@-@@");
        validator.validate(validatable);
        assertThat(validatable.getErrors()).isNotEmpty();
    }
}
