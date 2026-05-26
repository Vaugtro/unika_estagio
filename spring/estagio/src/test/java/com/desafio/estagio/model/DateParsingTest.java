package com.desafio.estagio.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("DD/MM/YYYY Date Parsing Tests")
class DateParsingTest {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Test
    @DisplayName("should parse valid dd/MM/yyyy date")
    void testParseValidDate() {
        LocalDate result = LocalDate.parse("15/08/1990", FORMATTER);
        assertThat(result).isEqualTo(LocalDate.of(1990, 8, 15));
    }

    @Test
    @DisplayName("should parse first day of month")
    void testParseFirstDayOfMonth() {
        LocalDate result = LocalDate.parse("01/01/2024", FORMATTER);
        assertThat(result).isEqualTo(LocalDate.of(2024, 1, 1));
    }

    @Test
    @DisplayName("should parse last day of month")
    void testParseLastDayOfMonth() {
        LocalDate result = LocalDate.parse("31/12/2025", FORMATTER);
        assertThat(result).isEqualTo(LocalDate.of(2025, 12, 31));
    }

    @Test
    @DisplayName("should parse leap year date (29/02/2024)")
    void testParseLeapYearDate() {
        LocalDate result = LocalDate.parse("29/02/2024", FORMATTER);
        assertThat(result).isEqualTo(LocalDate.of(2024, 2, 29));
    }

    @Test
    @DisplayName("should parse date with single digit day and month")
    void testParseSingleDigitDayMonth() {
        LocalDate result = LocalDate.parse("05/03/2000", FORMATTER);
        assertThat(result).isEqualTo(LocalDate.of(2000, 3, 5));
    }

    @Test
    @DisplayName("should reject invalid date format (MM/dd/yyyy)")
    void testRejectInvalidFormatMMddYYYY() {
        // MM/dd/yyyy — "15" would be parsed as month and fail
        assertThatThrownBy(() -> LocalDate.parse("08/15/1990", FORMATTER))
                .isInstanceOf(DateTimeException.class);
    }

    @Test
    @DisplayName("should reject invalid date format (yyyy/MM/dd)")
    void testRejectInvalidFormatYYYYMMdd() {
        assertThatThrownBy(() -> LocalDate.parse("1990/08/15", FORMATTER))
                .isInstanceOf(DateTimeException.class);
    }

    @Test
    @DisplayName("should reject invalid date format (dd-MM-yyyy)")
    void testRejectInvalidFormatddMMyyyyWithDashes() {
        assertThatThrownBy(() -> LocalDate.parse("15-08-1990", FORMATTER))
                .isInstanceOf(DateTimeException.class);
    }

    @Test
    @DisplayName("should reject invalid date (32/01/2024)")
    void testRejectInvalidDay() {
        assertThatThrownBy(() -> LocalDate.parse("32/01/2024", FORMATTER))
                .isInstanceOf(DateTimeException.class);
    }

    @Test
    @DisplayName("should reject invalid date (15/13/2024)")
    void testRejectInvalidMonth() {
        assertThatThrownBy(() -> LocalDate.parse("15/13/2024", FORMATTER))
                .isInstanceOf(DateTimeException.class);
    }

    @Test
    @DisplayName("should reject non-leap year date (29/02/2023) with strict resolver")
    void testRejectInvalidLeapYearDate() {
        DateTimeFormatter strictFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                .withResolverStyle(java.time.format.ResolverStyle.STRICT);
        assertThatThrownBy(() -> LocalDate.parse("29/02/2023", strictFormatter))
                .isInstanceOf(DateTimeException.class);
    }

    @Test
    @DisplayName("should accept 29/02/2023 with default SMART resolver (resolves to 28/02/2023)")
    void testSmartResolverAdjustsInvalidLeapYear() {
        LocalDate result = LocalDate.parse("29/02/2023", FORMATTER);
        assertThat(result).isEqualTo(LocalDate.of(2023, 2, 28));
    }

    @Test
    @DisplayName("should reject empty string")
    void testRejectEmptyString() {
        assertThatThrownBy(() -> LocalDate.parse("", FORMATTER))
                .isInstanceOf(DateTimeException.class);
    }

    @Test
    @DisplayName("should format LocalDate back to dd/MM/yyyy string")
    void testFormatLocalDateToString() {
        LocalDate date = LocalDate.of(1990, 8, 15);
        String formatted = date.format(FORMATTER);
        assertThat(formatted).isEqualTo("15/08/1990");
    }

    @Test
    @DisplayName("should roundtrip parse and format")
    void testRoundtripParseFormat() {
        String original = "25/12/2024";
        LocalDate parsed = LocalDate.parse(original, FORMATTER);
        String formatted = parsed.format(FORMATTER);
        assertThat(formatted).isEqualTo(original);
    }
}
