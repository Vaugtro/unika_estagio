package com.desafio.estagio.model.formatter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.*;

@RunWith(Parameterized.class)
public class CEPFormatterTest {

    private String input;
    private String expected;
    private String testName;

    public CEPFormatterTest(String testName, String input, String expected) {
        this.testName = testName;
        this.input = input;
        this.expected = expected;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "format valid 8-digit CEP", "01234567", "01234-567" },
                { "format various 8-char 1", "12345678", "12345-678" },
                { "format various 8-char 2", "00000000", "00000-000" },
                { "format various 8-char 3", "99999999", "99999-999" },
                { "format various 8-char 4", "01310100", "01310-100" },
                { "format invalid length short", "0123456", "0123456" },
                { "format invalid length long", "012345678", "012345678" },
                { "format empty", "", "" },
                { "unformat formatted CEP", "01234-567", "01234567" },
                { "unformat with spaces", "01234 567", "01234567" },
                { "unformat with dots", "01234.567", "01234567" },
                { "unformat with slashes", "01234/567", "01234567" },
                { "unformat already unformatted", "01234567", "01234567" }
        });
    }

    @Test
    public void testFormatAndUnformat() {
        if (testName.startsWith("format")) {
            String result = CEPFormatter.format(input);
            assertThat(result).as(testName).isEqualTo(expected);
        } else if (testName.startsWith("unformat")) {
            String result = CEPFormatter.unformat(input);
            assertThat(result).as(testName).isEqualTo(expected);
        }
    }

    @Test
    public void testFormatNull() {
        String result = CEPFormatter.format(null);
        assertThat(result).isNull();
    }

    @Test
    public void testUnformatNull() {
        String result = CEPFormatter.unformat(null);
        assertThat(result).isNull();
    }

    @Test
    public void testUnformatEmpty() {
        String result = CEPFormatter.unformat("");
        assertThat(result).isEmpty();
    }

    @Test
    public void testUnformatOnlySpecialCharacters() {
        String result = CEPFormatter.unformat("-./");
        assertThat(result).isEmpty();
    }

    @Test
    public void testUnformatMixedCharacters() {
        String result = CEPFormatter.unformat("0-1.2/3 4-5 6 7 8");
        assertThat(result).isEqualTo("012345678");
    }

    @Test
    public void testRoundtripFormatUnformat() {
        String original = "01234567";
        String formatted = CEPFormatter.format(original);
        String unformatted = CEPFormatter.unformat(formatted);
        assertThat(unformatted).isEqualTo(original);
    }

    @Test
    public void testRoundtripUnformatFormat() {
        String original = "01234-567";
        String unformatted = CEPFormatter.unformat(original);
        String formatted = CEPFormatter.format(unformatted);
        assertThat(formatted).isEqualTo(original);
    }
}
