package com.desafio.estagio.validation.internal;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RGValidatorTest {

    private RGValidator validator;

    @Before
    public void setUp() {
        validator = new RGValidator();
    }

    @Test
    public void shouldRejectNullInput() {
        // Arrange & Act
        boolean result = validator.isValid(null, null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectEmptyInput() {
        // Arrange & Act
        boolean result = validator.isValid("", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectBlankInput() {
        // Arrange & Act
        boolean result = validator.isValid("   ", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldAcceptValid7DigitRG() {
        // Arrange & Act
        boolean result = validator.isValid("1234567", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptValid7DigitRGVariant() {
        // Arrange & Act
        boolean result = validator.isValid("9876543", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptValid8DigitRG() {
        // Arrange & Act
        boolean result = validator.isValid("12345678", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptValid8DigitRGVariant() {
        // Arrange & Act
        boolean result = validator.isValid("98765432", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptValid9DigitRG() {
        // Arrange & Act
        boolean result = validator.isValid("123456789", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptValid9DigitRGVariant() {
        // Arrange & Act
        boolean result = validator.isValid("987654321", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptFormattedRGWithDots() {
        // Arrange & Act
        boolean result = validator.isValid("12.345.678", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptFormattedRGWithDotsAndDash() {
        // Arrange & Act
        boolean result = validator.isValid("12.345.678-9", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptFormattedRGWithDashes() {
        // Arrange & Act
        boolean result = validator.isValid("12-345-678", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptFormattedRGWithSpaces() {
        // Arrange & Act
        boolean result = validator.isValid("12 345 678", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldRejectAllOnes7Digits() {
        // Arrange & Act
        boolean result = validator.isValid("1111111", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectAllTwos7Digits() {
        // Arrange & Act
        boolean result = validator.isValid("2222222", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectAllOnes8Digits() {
        // Arrange & Act
        boolean result = validator.isValid("11111111", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectAllTwos8Digits() {
        // Arrange & Act
        boolean result = validator.isValid("22222222", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectAllOnes9Digits() {
        // Arrange & Act
        boolean result = validator.isValid("111111111", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectAllTwos9Digits() {
        // Arrange & Act
        boolean result = validator.isValid("222222222", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectFormattedAllSameDigits() {
        // Arrange & Act
        boolean result = validator.isValid("1.111.111", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectFormattedAllSameDigitsWithDash() {
        // Arrange & Act
        boolean result = validator.isValid("1.111.111-1", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectTooShort1Digit() {
        // Arrange & Act
        boolean result = validator.isValid("1", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectTooShort2Digits() {
        // Arrange & Act
        boolean result = validator.isValid("12", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectTooShort3Digits() {
        // Arrange & Act
        boolean result = validator.isValid("123", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectTooShort4Digits() {
        // Arrange & Act
        boolean result = validator.isValid("1234", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectTooShort5Digits() {
        // Arrange & Act
        boolean result = validator.isValid("12345", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectTooShort6Digits() {
        // Arrange & Act
        boolean result = validator.isValid("123456", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectTooLong10Digits() {
        // Arrange & Act
        boolean result = validator.isValid("1234567890", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectTooLong11Digits() {
        // Arrange & Act
        boolean result = validator.isValid("12345678901", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectTooLong12Digits() {
        // Arrange & Act
        boolean result = validator.isValid("123456789012", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectRGWithLetters() {
        // Arrange & Act
        boolean result = validator.isValid("123456a", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectRGWithLettersVariant() {
        // Arrange & Act
        boolean result = validator.isValid("1234567b", null);

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
    public void shouldRejectSpecialCharactersOnly() {
        // Arrange & Act
        boolean result = validator.isValid("@@@@@@@", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectSpecialCharactersOnly2() {
        // Arrange & Act
        boolean result = validator.isValid("#######", null);

        // Assert
        assertThat(result).isFalse();
    }
}
