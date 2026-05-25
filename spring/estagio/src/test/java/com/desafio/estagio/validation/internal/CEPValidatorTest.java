package com.desafio.estagio.validation.internal;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CEPValidatorTest {

    private CEPValidator validator;

    @Before
    public void setUp() {
        validator = new CEPValidator();
    }

    @Test
    public void shouldAcceptNullInput() {
        // Arrange & Act
        boolean result = validator.isValid(null, null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptEmptyInput() {
        // Arrange & Act
        boolean result = validator.isValid("", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptValidCEPWithoutDash() {
        // Arrange & Act
        boolean result = validator.isValid("12345678", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptValidCEPWithDash() {
        // Arrange & Act
        boolean result = validator.isValid("12345-678", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptValidSaoPauloCEP() {
        // Arrange & Act
        boolean result = validator.isValid("01310-100", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptValidRioCEP() {
        // Arrange & Act
        boolean result = validator.isValid("20040020", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptAllZeros() {
        // Arrange & Act
        boolean result = validator.isValid("00000000", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptAllNines() {
        // Arrange & Act
        boolean result = validator.isValid("99999999", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldRejectTooShortCEP() {
        // Arrange & Act
        boolean result = validator.isValid("1234567", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectTooLongCEP() {
        // Arrange & Act
        boolean result = validator.isValid("123456789", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectDashInWrongPosition1() {
        // Arrange & Act
        boolean result = validator.isValid("12345-67", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectDashInWrongPosition2() {
        // Arrange & Act
        boolean result = validator.isValid("1234-5678", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectDoubleDash() {
        // Arrange & Act
        boolean result = validator.isValid("12345--678", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectCEPWithLetter() {
        // Arrange & Act
        boolean result = validator.isValid("1234a678", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectCEPWithSpace() {
        // Arrange & Act
        boolean result = validator.isValid("12345 678", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectCEPWithDot() {
        // Arrange & Act
        boolean result = validator.isValid("12345.678", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectAllLetters() {
        // Arrange & Act
        boolean result = validator.isValid("abcdefgh", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectIncomplete() {
        // Arrange & Act
        boolean result = validator.isValid("12345-", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectDashAtStart() {
        // Arrange & Act
        boolean result = validator.isValid("-12345678", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectDashAtEnd() {
        // Arrange & Act
        boolean result = validator.isValid("12345678-", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectCEPWithSpecialCharacters() {
        // Arrange & Act
        boolean result = validator.isValid("12345@678", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectCEPWithLeadingSpace() {
        // Arrange & Act
        boolean result = validator.isValid(" 12345678", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectCEPWithTrailingSpace() {
        // Arrange & Act
        boolean result = validator.isValid("12345678 ", null);

        // Assert
        assertThat(result).isFalse();
    }
}
