package org.semver4j.internal;

import static java.lang.Integer.parseInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.semver4j.internal.Utils.*;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class UtilsTest {
    private static final int X_RANGE_MARKER = -1;

    @ParameterizedTest
    @ValueSource(strings = {"x", "X", "*", "+"})
    void shouldReturnXRangeMarkerWhenWildcardIsProvided(String wildcard) {
        // when
        int result = parseIntWithXSupport(wildcard);

        // then
        assertThat(result).isEqualTo(X_RANGE_MARKER);
    }

    @Test
    void shouldReturnXRangeMarkerWhenNullIsProvided() {
        // when
        int result = parseIntWithXSupport(null);

        // then
        assertThat(result).isEqualTo(X_RANGE_MARKER);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "42", "0", "-5"})
    void shouldReturnParsedIntWhenValidNumberIsProvided(String number) {
        // when
        int result = parseIntWithXSupport(number);

        // then
        assertThat(result).isEqualTo(parseInt(number));
    }

    @Test
    void shouldThrowNumberFormatExceptionWhenInvalidNumberIsProvided() {
        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> parseIntWithXSupport("not-a-number");

        // then
        assertThatCode(throwingCallable).isInstanceOf(NumberFormatException.class);
    }

    @Test
    void shouldReturnTrueWhenValueIsXRangeMarker() {
        // when
        boolean result = isX(X_RANGE_MARKER);

        // then
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, -2, 42})
    void shouldReturnFalseWhenValueIsNotXRangeMarker(int value) {
        // when
        boolean result = isX(value);

        // then
        assertThat(result).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "test", " "})
    void shouldReturnTrueWhenStringIsNotEmpty(String value) {
        // when
        boolean result = isNotBlank(value);

        // then
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldReturnFalseWhenStringIsNullOrEmpty(String value) {
        // when
        boolean result = isNotBlank(value);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldHaveCorrectValues() {
        // then
        assertThat(EMPTY).isEmpty();
        assertThat(SPACE).isEqualTo(" ");
        assertThat(ALL_RANGE).isEqualTo(">=0.0.0");
        assertThat(ALL_RANGE_WITH_PRERELEASE).startsWith(">=0.0.0");
    }
}
