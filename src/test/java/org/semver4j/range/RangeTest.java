package org.semver4j.range;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.semver4j.range.Range.RangeOperator.*;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.semver4j.Semver;
import org.semver4j.range.Range.RangeOperator;

class RangeTest {
    @Test
    void shouldCheckSatisfiedByAny() {
        // given
        Range range = new Range(Semver.ZERO, GTE);

        // when/then
        assertThat(range.isSatisfiedByAny()).isTrue();
    }

    @Test
    void shouldCheckSatisfiedByForEqual() {
        // given
        Range range = new Range("1.2.3", EQ);

        // when/then
        assertThat(range.isSatisfiedBy("1.2.3")).isTrue();

        assertThat(range.isSatisfiedBy("2.2.3")).isFalse();
        assertThat(range.isSatisfiedBy("1.3.3")).isFalse();
        assertThat(range.isSatisfiedBy("1.2.4")).isFalse();

        assertThat(range.isSatisfiedBy("0.2.3")).isFalse();
        assertThat(range.isSatisfiedBy("1.1.3")).isFalse();
        assertThat(range.isSatisfiedBy("1.2.2")).isFalse();
    }

    @Test
    void shouldCheckSatisfiedByForEqualWithPreRelease() {
        // given
        Range range = new Range("1.2.3-alpha", EQ);

        // when/then
        assertFalse(range.isSatisfiedBy("1.2.3"));
        assertFalse(range.isSatisfiedBy("1.2.3-beta"));
    }

    @Test
    void shouldCheckSatisfiedByForLowerThan() {
        // given
        Range range = new Range("1.2.3", LT);

        // when/then
        assertThat(range.isSatisfiedBy("1.2.3")).isFalse();
        assertThat(range.isSatisfiedBy("1.2.4")).isFalse();
        assertThat(range.isSatisfiedBy("1.2.2")).isTrue();
    }

    @Test
    void shouldCheckSatisfiedByForLowerThanOrEqual() {
        // given
        Range range = new Range("1.2.3", LTE);

        // when/then
        assertThat(range.isSatisfiedBy("1.2.4")).isFalse();
        assertThat(range.isSatisfiedBy("1.2.3")).isTrue();
        assertThat(range.isSatisfiedBy("1.2.2")).isTrue();
    }

    @Test
    void shouldCheckSatisfiedByForGreaterThan() {
        // given
        Range range = new Range("1.2.3", GT);

        // when/then
        assertThat(range.isSatisfiedBy("1.2.3")).isFalse();
        assertThat(range.isSatisfiedBy("1.2.2")).isFalse();
        assertThat(range.isSatisfiedBy("1.2.4")).isTrue();
    }

    @Test
    void shouldCheckSatisfiedByForGreaterThanOrEqual() {
        // given
        Range range = new Range("1.2.3", GTE);

        // when/then
        assertThat(range.isSatisfiedBy("1.2.2")).isFalse();
        assertThat(range.isSatisfiedBy("1.2.3")).isTrue();
        assertThat(range.isSatisfiedBy("1.2.4")).isTrue();
    }

    @ParameterizedTest
    @MethodSource("prettyPrint")
    void shouldPrettyPrintRange(String expected, RangeOperator rangeOperator) {
        // given
        Range range = new Range("1.2.3", rangeOperator);

        // when
        String actual = range.toString();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    static Stream<Arguments> prettyPrint() {
        return Stream.of(
                arguments("=1.2.3", EQ),
                arguments("<1.2.3", LT),
                arguments("<=1.2.3", LTE),
                arguments(">1.2.3", GT),
                arguments(">=1.2.3", GTE));
    }
}
