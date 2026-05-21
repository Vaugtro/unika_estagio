package com.desafio.estagio.model.formatter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.*;

@RunWith(Parameterized.class)
public class CNPJFormatterTest {

    private String input;
    private String expected;
    private String testName;

    public CNPJFormatterTest(String testName, String input, String expected) {
        this.testName = testName;
        this.input = input;
        this.expected = expected;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "format valid 14-digit CNPJ", "12345678000190", "12.345.678/0001-90" },
                { "format with dots and slashes", "12.345.678/0001-90", "12.345.678/0001-90" },
                { "format with dashes", "12-345-678-0001-90", "12-345-678-0001-90" },
                { "format with spaces", "12 345 678 0001 90", "12 345 678 0001 90" },
                { "format invalid length short", "1234567800019", "1234567800019" },
                { "format invalid length long", "123456780001900", "123456780001900" },
                { "unformat formatted CNPJ", "12.345.678/0001-90", "12345678000190" },
                { "unformat with dashes", "12-345-678-0001-90", "12345678000190" },
                { "unformat with spaces", "12 345 678 0001 90", "12345678000190" },
                { "unformat already unformatted", "12345678000190", "12345678000190" }
        });
    }

    @Test
    public void testFormatAndUnformat() {
        if (testName.startsWith("format")) {
            String result = CNPJFormatter.format(input);
            assertThat(result).as(testName).isEqualTo(expected);
        } else if (testName.startsWith("unformat")) {
            String result = CNPJFormatter.unformat(input);
            assertThat(result).as(testName).isEqualTo(expected);
        }
    }

    @Test
    public void testFormatNull() {
        String result = CNPJFormatter.format(null);
        assertThat(result).isNull();
    }

    @Test
    public void testUnformatNull() {
        String result = CNPJFormatter.unformat(null);
        assertThat(result).isNull();
    }

    @Test
    public void testUnformatEmpty() {
        String result = CNPJFormatter.unformat("");
        assertThat(result).isEmpty();
    }

    @Test
    public void testIsValidWithValidCNPJ() {
        boolean result = CNPJFormatter.isValid("11222333000181");
        assertThat(result).isTrue();
    }

    @Test
    public void testIsValidWithFormattedValidCNPJ() {
        boolean result = CNPJFormatter.isValid("11.222.333/0001-81");
        assertThat(result).isTrue();
    }

    @Test
    public void testIsValidWithInvalidCNPJ1() {
        boolean result = CNPJFormatter.isValid("12345678000190");
        assertThat(result).isFalse();
    }

    @Test
    public void testIsValidWithInvalidCNPJ2() {
        boolean result = CNPJFormatter.isValid("00000000000000");
        assertThat(result).isTrue();
    }

    @Test
    public void testIsValidWithInvalidCNPJ3() {
        boolean result = CNPJFormatter.isValid("11111111111111");
        assertThat(result).isFalse();
    }

    @Test
    public void testIsValidWithInvalidCNPJ4() {
        boolean result = CNPJFormatter.isValid("12345678901234");
        assertThat(result).isFalse();
    }

    @Test
    public void testIsValidWithInvalidCNPJ5() {
        boolean result = CNPJFormatter.isValid("123456789012");
        assertThat(result).isFalse();
    }

    @Test
    public void testIsValidNull() {
        boolean result = CNPJFormatter.isValid(null);
        assertThat(result).isFalse();
    }

    @Test
    public void testIsValidEmpty() {
        boolean result = CNPJFormatter.isValid("");
        assertThat(result).isFalse();
    }

    @Test
    public void testMaskValidCNPJ() {
        String result = CNPJFormatter.mask("12345678000190");
        assertThat(result).isEqualTo("12.345.***/****-90");
    }

    @Test
    public void testMaskFormattedCNPJ() {
        String result = CNPJFormatter.mask("12.345.678/0001-90");
        assertThat(result).isEqualTo("12.345.***/****-90");
    }

    @Test
    public void testMaskNull() {
        String result = CNPJFormatter.mask(null);
        assertThat(result).isNull();
    }

    @Test
    public void testMaskInvalidLength() {
        String result = CNPJFormatter.mask("1234567800019");
        assertThat(result).isEqualTo("1234567800019");
    }

    @Test
    public void testPrivateConstructorThrowsException() {
        assertThatThrownBy(() -> {
            Constructor<?> constructor = CNPJFormatter.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        }).isInstanceOf(Exception.class)
         .hasCauseInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void testRoundtripFormatUnformat() {
        String original = "11222333000181";
        String formatted = CNPJFormatter.format(original);
        String unformatted = CNPJFormatter.unformat(formatted);
        assertThat(unformatted).isEqualTo(original);
    }

    @Test
    public void testMaskUnformatRoundtrip() {
        String original = "11222333000181";
        String masked = CNPJFormatter.mask(original);
        String unformatted = CNPJFormatter.unformat(masked);
        assertThat(unformatted).isEqualTo("1122281");
    }
}
