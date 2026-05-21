package com.desafio.estagio.model.formatter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.*;

@RunWith(Parameterized.class)
public class TelefoneFormatterTest {

    private String input;
    private String expected;
    private String testName;

    public TelefoneFormatterTest(String testName, String input, String expected) {
        this.testName = testName;
        this.input = input;
        this.expected = expected;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "format valid 10-digit", "1134567890", "(11) 3456-7890" },
                { "format various 10-digit 1", "2133334444", "(21) 3333-4444" },
                { "format various 10-digit 2", "8532221111", "(85) 3222-1111" },
                { "format various 10-digit 3", "1100000000", "(11) 0000-0000" },
                { "format valid 11-digit", "11912345678", "(11) 91234-5678" },
                { "format various 11-digit 1", "21987654321", "(21) 98765-4321" },
                { "format various 11-digit 2", "85988887777", "(85) 98888-7777" },
                { "format various 11-digit 3", "11900000000", "(11) 90000-0000" },
                { "format invalid length short", "113456789", "113456789" },
                { "format invalid length long", "119123456789", "119123456789" },
                { "format empty", "", "" },
                { "unformat 10-digit formatted", "(11) 3456-7890", "1134567890" },
                { "unformat 11-digit formatted", "(11) 91234-5678", "11912345678" },
                { "unformat with spaces", "11 3456-7890", "1134567890" },
                { "unformat with dashes", "11-3456-7890", "1134567890" },
                { "unformat already unformatted 10", "1134567890", "1134567890" },
                { "unformat already unformatted 11", "11912345678", "11912345678" }
        });
    }

    @Test
    public void testFormatAndUnformat() {
        if (testName.startsWith("format")) {
            String result = TelefoneFormatter.format(input);
            assertThat(result).as(testName).isEqualTo(expected);
        } else if (testName.startsWith("unformat")) {
            String result = TelefoneFormatter.unformat(input);
            assertThat(result).as(testName).isEqualTo(expected);
        }
    }

    @Test
    public void testFormatNull() {
        String result = TelefoneFormatter.format(null);
        assertThat(result).isNull();
    }

    @Test
    public void testUnformatNull() {
        String result = TelefoneFormatter.unformat(null);
        assertThat(result).isNull();
    }

    @Test
    public void testUnformatEmpty() {
        String result = TelefoneFormatter.unformat("");
        assertThat(result).isEmpty();
    }

    @Test
    public void testUnformatOnlySpecialCharacters() {
        String result = TelefoneFormatter.unformat("()- ");
        assertThat(result).isEmpty();
    }

    @Test
    public void testRoundtripFormatUnformat10Digits() {
        String original = "1134567890";
        String formatted = TelefoneFormatter.format(original);
        String unformatted = TelefoneFormatter.unformat(formatted);
        assertThat(unformatted).isEqualTo(original);
    }

    @Test
    public void testRoundtripFormatUnformat11Digits() {
        String original = "11912345678";
        String formatted = TelefoneFormatter.format(original);
        String unformatted = TelefoneFormatter.unformat(formatted);
        assertThat(unformatted).isEqualTo(original);
    }

    @Test
    public void testRoundtripUnformatFormat10Digits() {
        String original = "(11) 3456-7890";
        String unformatted = TelefoneFormatter.unformat(original);
        String formatted = TelefoneFormatter.format(unformatted);
        assertThat(formatted).isEqualTo(original);
    }

    @Test
    public void testRoundtripUnformatFormat11Digits() {
        String original = "(11) 91234-5678";
        String unformatted = TelefoneFormatter.unformat(original);
        String formatted = TelefoneFormatter.format(unformatted);
        assertThat(formatted).isEqualTo(original);
    }
}
