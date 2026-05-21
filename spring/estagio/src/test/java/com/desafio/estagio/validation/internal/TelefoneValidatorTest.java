package com.desafio.estagio.validation.internal;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TelefoneValidatorTest {

    private TelefoneValidator validator;

    @Before
    public void setUp() {
        validator = new TelefoneValidator();
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
    public void shouldAcceptValid10DigitSaoPauloLandline() {
        // Arrange & Act
        boolean result = validator.isValid("1122334455", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptValid10DigitSaoPauloLandlineVariant() {
        // Arrange & Act
        boolean result = validator.isValid("1133334455", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptValid10DigitSaoPauloLandlineVariant2() {
        // Arrange & Act
        boolean result = validator.isValid("1144334455", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptValid10DigitSaoPauloLandlineVariant3() {
        // Arrange & Act
        boolean result = validator.isValid("1155334455", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptValid10DigitSaoPauloLandlineVariant4() {
        // Arrange & Act
        boolean result = validator.isValid("1166334455", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptValid10DigitSaoPauloLandlineVariant5() {
        // Arrange & Act
        boolean result = validator.isValid("1177334455", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptValid10DigitSaoPauloLandlineVariant6() {
        // Arrange & Act
        boolean result = validator.isValid("1188334455", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptValid10DigitRioLandline() {
        // Arrange & Act
        boolean result = validator.isValid("2122334455", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptValid10DigitCearaLandline() {
        // Arrange & Act
        boolean result = validator.isValid("8532334455", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptValid10DigitAmazonasLandline() {
        // Arrange & Act
        boolean result = validator.isValid("9922334455", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptValid11DigitSaoPauloMobile() {
        // Arrange & Act
        boolean result = validator.isValid("11922334455", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptValid11DigitSaoPauloMobileVariant() {
        // Arrange & Act
        boolean result = validator.isValid("11932334455", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptValid11DigitSaoPauloMobileVariant2() {
        // Arrange & Act
        boolean result = validator.isValid("11942334455", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptValid11DigitSaoPauloMobileVariant3() {
        // Arrange & Act
        boolean result = validator.isValid("11952334455", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptValid11DigitSaoPauloMobileVariant4() {
        // Arrange & Act
        boolean result = validator.isValid("11962334455", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptValid11DigitSaoPauloMobileVariant5() {
        // Arrange & Act
        boolean result = validator.isValid("11972334455", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptValid11DigitSaoPauloMobileVariant6() {
        // Arrange & Act
        boolean result = validator.isValid("11982334455", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptValid11DigitRioMobile() {
        // Arrange & Act
        boolean result = validator.isValid("21987654321", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptValid11DigitCearaMobile() {
        // Arrange & Act
        boolean result = validator.isValid("85988776655", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptValid11DigitAmazonasMobile() {
        // Arrange & Act
        boolean result = validator.isValid("99987654321", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptFormattedPhone1() {
        // Arrange & Act
        boolean result = validator.isValid("(11) 2233-4455", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptFormattedPhone2() {
        // Arrange & Act
        boolean result = validator.isValid("(11) 92233-4455", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptFormattedPhone3() {
        // Arrange & Act
        boolean result = validator.isValid("11 2233-4455", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptFormattedPhone4() {
        // Arrange & Act
        boolean result = validator.isValid("11 92233-4455", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptFormattedPhone5() {
        // Arrange & Act
        boolean result = validator.isValid("(21) 3333-4455", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptFormattedPhone6() {
        // Arrange & Act
        boolean result = validator.isValid("(21) 98765-4321", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldRejectInvalidAreaCodeStartingWith0() {
        // Arrange & Act
        boolean result = validator.isValid("0122334455", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectInvalidAreaCodeStartingWith0Variant() {
        // Arrange & Act
        boolean result = validator.isValid("0192334455", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectInvalidAreaCodeStartingWith1() {
        // Arrange & Act
        boolean result = validator.isValid("1022334455", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectInvalidAreaCodeStartingWith1Variant() {
        // Arrange & Act
        boolean result = validator.isValid("1092334455", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectInvalidFirstDigitAfterAreaCode0() {
        // Arrange & Act
        boolean result = validator.isValid("1102334455", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectInvalidFirstDigitAfterAreaCode1() {
        // Arrange & Act
        boolean result = validator.isValid("1112334455", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectInvalidFirstDigitAfterAreaCode9With10Digits() {
        // Arrange & Act
        boolean result = validator.isValid("1192334455", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectInvalidFirstDigitAfterAreaCode0Rio() {
        // Arrange & Act
        boolean result = validator.isValid("2102334455", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectInvalidFirstDigitAfterAreaCode1Rio() {
        // Arrange & Act
        boolean result = validator.isValid("2112334455", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectTooFewDigits6() {
        // Arrange & Act
        boolean result = validator.isValid("112233", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectTooFewDigits7() {
        // Arrange & Act
        boolean result = validator.isValid("1122334", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectTooFewDigits8() {
        // Arrange & Act
        boolean result = validator.isValid("11223344", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectTooFewDigits9() {
        // Arrange & Act
        boolean result = validator.isValid("112233445", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectTooManyDigits12() {
        // Arrange & Act
        boolean result = validator.isValid("119223344556", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectTooManyDigits13() {
        // Arrange & Act
        boolean result = validator.isValid("1192233445567", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectTooManyDigits14() {
        // Arrange & Act
        boolean result = validator.isValid("11922334455678", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectPhoneWithLetters1() {
        // Arrange & Act
        boolean result = validator.isValid("1122334a55", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectPhoneWithLetters2() {
        // Arrange & Act
        boolean result = validator.isValid("11223344b5", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectPhoneWithLetters3() {
        // Arrange & Act
        boolean result = validator.isValid("abcdefghij", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectPhoneWithLetters4() {
        // Arrange & Act
        boolean result = validator.isValid("1122334c55d", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectSpecialCharactersOnly1() {
        // Arrange & Act
        boolean result = validator.isValid("(11) ####-####", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectSpecialCharactersOnly2() {
        // Arrange & Act
        boolean result = validator.isValid("@@@@@@@@@@", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldRejectSpecialCharactersOnly3() {
        // Arrange & Act
        boolean result = validator.isValid("##########", null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void shouldAcceptMixedFormattedPhone1() {
        // Arrange & Act
        boolean result = validator.isValid("11-92233-4455", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptMixedFormattedPhone2() {
        // Arrange & Act
        boolean result = validator.isValid("11.92233.4455", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptMixedFormattedPhone3() {
        // Arrange & Act
        boolean result = validator.isValid("11 92233 4455", null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptMixedFormattedPhone4() {
        // Arrange & Act
        boolean result = validator.isValid("(11)92233-4455", null);

        // Assert
        assertThat(result).isTrue();
    }
}
