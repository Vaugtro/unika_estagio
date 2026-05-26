package com.desafio.estagio.model.formatter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class RGFormatterTest {

    private final String input;
    private final String expected;
    private final String testName;

    public RGFormatterTest(String testName, String input, String expected) {
        this.testName = testName;
        this.input = input;
        this.expected = expected;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                // 9-digit RG: XX.XXX.XXX-X
                {"format 9-digit RG", "123456789", "12.345.678-9"},
                {"format various 9-digit 1", "998877665", "99.887.766-5"},
                {"format various 9-digit 2", "000000000", "00.000.000-0"},
                // 8-digit RG: XX.XXX.XX-X
                {"format 8-digit RG", "12345678", "12.345.67-8"},
                {"format various 8-digit 1", "99887766", "99.887.76-6"},
                {"format various 8-digit 2", "00000000", "00.000.00-0"},
                // 7-digit RG: X.XXX-XXX
                {"format 7-digit RG", "1234567", "1.234-567"},
                {"format various 7-digit 1", "9988776", "9.988-776"},
                {"format various 7-digit 2", "0000000", "0.000-000"},
                // Pre-formatted RGs should pass through (already formatted)
                {"format already formatted 9-digit", "12.345.678-9", "12.345.678-9"},
                {"format already formatted 8-digit", "12.345.67-8", "12.345.67-8"},
                {"format already formatted 7-digit", "1.234-567", "1.234-567"},
                // Invalid lengths return as-is
                {"format invalid length short", "123456", "123456"},
                {"format invalid length long", "1234567890", "1234567890"},
                {"format empty", "", ""},
                // Unformat
                {"unformat 9-digit formatted", "12.345.678-9", "123456789"},
                {"unformat 8-digit formatted", "12.345.67-8", "12345678"},
                {"unformat 7-digit formatted", "1.234-567", "1234567"},
                {"unformat with dots only", "12.345.678.9", "123456789"},
                {"unformat with dashes only", "12-345-678-9", "123456789"},
                {"unformat already unformatted 9", "123456789", "123456789"},
                {"unformat already unformatted 8", "12345678", "12345678"},
                {"unformat already unformatted 7", "1234567", "1234567"}
        });
    }

    @Test
    public void testFormatAndUnformat() {
        if (testName.startsWith("format")) {
            String result = RGFormatter.format(input);
            assertThat(result).as(testName).isEqualTo(expected);
        } else if (testName.startsWith("unformat")) {
            String result = RGFormatter.unformat(input);
            assertThat(result).as(testName).isEqualTo(expected);
        }
    }

    @Test
    public void testFormatNull() {
        String result = RGFormatter.format(null);
        assertThat(result).isNull();
    }

    @Test
    public void testFormatBlank() {
        String result = RGFormatter.format("   ");
        assertThat(result).isEqualTo("   ");
    }

    @Test
    public void testFormatEmpty() {
        String result = RGFormatter.format("");
        assertThat(result).isEmpty();
    }

    @Test
    public void testUnformatNull() {
        String result = RGFormatter.unformat(null);
        assertThat(result).isNull();
    }

    @Test
    public void testUnformatEmpty() {
        String result = RGFormatter.unformat("");
        assertThat(result).isEmpty();
    }

    @Test
    public void testUnformatOnlySpecialCharacters() {
        String result = RGFormatter.unformat(".-.");
        assertThat(result).isEmpty();
    }

    @Test
    public void testRoundtripFormatUnformat9Digits() {
        String original = "123456789";
        String formatted = RGFormatter.format(original);
        String unformatted = RGFormatter.unformat(formatted);
        assertThat(unformatted).isEqualTo(original);
    }

    @Test
    public void testRoundtripFormatUnformat8Digits() {
        String original = "12345678";
        String formatted = RGFormatter.format(original);
        String unformatted = RGFormatter.unformat(formatted);
        assertThat(unformatted).isEqualTo(original);
    }

    @Test
    public void testRoundtripFormatUnformat7Digits() {
        String original = "1234567";
        String formatted = RGFormatter.format(original);
        String unformatted = RGFormatter.unformat(formatted);
        assertThat(unformatted).isEqualTo(original);
    }

    @Test
    public void testRoundtripUnformatFormat9Digits() {
        String original = "12.345.678-9";
        String unformatted = RGFormatter.unformat(original);
        String formatted = RGFormatter.format(unformatted);
        assertThat(formatted).isEqualTo(original);
    }

    @Test
    public void testRoundtripUnformatFormat8Digits() {
        String original = "12.345.67-8";
        String unformatted = RGFormatter.unformat(original);
        String formatted = RGFormatter.format(unformatted);
        assertThat(formatted).isEqualTo(original);
    }

    @Test
    public void testRoundtripUnformatFormat7Digits() {
        String original = "1.234-567";
        String unformatted = RGFormatter.unformat(original);
        String formatted = RGFormatter.format(unformatted);
        assertThat(formatted).isEqualTo(original);
    }
}
