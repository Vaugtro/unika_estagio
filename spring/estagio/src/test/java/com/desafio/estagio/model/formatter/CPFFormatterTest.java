package com.desafio.estagio.model.formatter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class CPFFormatterTest {

    private final String input;
    private final String expected;
    private final String testName;

    public CPFFormatterTest(String testName, String input, String expected) {
        this.testName = testName;
        this.input = input;
        this.expected = expected;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"format valid 11-digit CPF", "12345678901", "123.456.789-01"},
                {"format with dots and dashes", "123.456.789-01", "123.456.789-01"},
                {"format with dashes only", "123-456-789-01", "123.456.789-01"},
                {"format with spaces", "123 456 789 01", "123.456.789-01"},
                {"format invalid length short", "1234567890", "1234567890"},
                {"format invalid length long", "123456789012", "123456789012"},
                {"unformat formatted CPF", "123.456.789-01", "12345678901"},
                {"unformat with dashes", "123-456-789-01", "12345678901"},
                {"unformat with spaces", "123 456 789 01", "12345678901"},
                {"unformat already unformatted", "12345678901", "12345678901"}
        });
    }

    @Test
    public void testFormatAndUnformat() {
        if (testName.startsWith("format")) {
            String result = CPFFormatter.format(input);
            assertThat(result).as(testName).isEqualTo(expected);
        } else if (testName.startsWith("unformat")) {
            String result = CPFFormatter.unformat(input);
            assertThat(result).as(testName).isEqualTo(expected);
        }
    }

    @Test
    public void testFormatNull() {
        String result = CPFFormatter.format(null);
        assertThat(result).isNull();
    }

    @Test
    public void testFormatBlank() {
        String result = CPFFormatter.format("   ");
        assertThat(result).isNull();
    }

    @Test
    public void testFormatEmpty() {
        String result = CPFFormatter.format("");
        assertThat(result).isNull();
    }

    @Test
    public void testUnformatNull() {
        String result = CPFFormatter.unformat(null);
        assertThat(result).isNull();
    }

    @Test
    public void testUnformatEmpty() {
        String result = CPFFormatter.unformat("");
        assertThat(result).isEmpty();
    }

    @Test
    public void testUnformatOnlySpecialCharacters() {
        String result = CPFFormatter.unformat(".-.-.-");
        assertThat(result).isEmpty();
    }

    @Test
    public void testRoundtripFormatUnformat() {
        String original = "12345678901";
        String formatted = CPFFormatter.format(original);
        String unformatted = CPFFormatter.unformat(formatted);
        assertThat(unformatted).isEqualTo(original);
    }
}
