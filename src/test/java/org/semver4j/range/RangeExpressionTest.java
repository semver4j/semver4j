package org.semver4j.range;

import static org.assertj.core.api.Assertions.assertThat;
import static org.semver4j.range.RangeExpression.*;

import org.junit.jupiter.api.Test;

class RangeExpressionTest {
    @Test
    void shouldHandleRangeWithOneExpression() {
        // given
        RangeExpression rangeExpressions = eq("1.0.0");

        // when
        RangeList rangeList = rangeExpressions.get();

        // then
        assertThat(rangeList.toString()).isEqualTo("=1.0.0");
    }

    @Test
    void shouldComplexRangeExpression() {
        // given
        RangeExpression rangeExpressions = eq("1.0.0")
                .and(eq("2.0.0")
                        .and(eq("3.0.0"))
                        .or(eq("4.0.0").and(eq("5.0.0")))
                        .or(eq("6.0.0")))
                .and(less("7.0.0"))
                .and(eq("8.0.0"))
                .or(less("9.0.0"));

        // when
        RangeList rangeList = rangeExpressions.get();

        // then
        assertThat(rangeList.toString())
                .isEqualTo(
                        "(=1.0.0 and =2.0.0 and =3.0.0) or (=4.0.0 and =5.0.0) or =6.0.0 or (<7.0.0 and =8.0.0) or <9.0.0");
    }

    @Test
    void shouldGenerateEqualExpression() {
        // when
        RangeExpression rangeExpression = eq("1.0.0");

        // then
        assertThat(rangeExpression.get().toString()).isEqualTo("=1.0.0");
    }

    @Test
    void shouldGenerateGreaterExpression() {
        // when
        RangeExpression rangeExpression = greater("1.0.0");

        // then
        assertThat(rangeExpression.get().toString()).isEqualTo(">1.0.0");
    }

    @Test
    void shouldGenerateGreaterOrEqualExpression() {
        // when
        RangeExpression rangeExpression = greaterOrEqual("1.0.0");

        // then
        assertThat(rangeExpression.get().toString()).isEqualTo(">=1.0.0");
    }

    @Test
    void shouldGenerateLessExpression() {
        // when
        RangeExpression rangeExpression = less("1.0.0");

        // then
        assertThat(rangeExpression.get().toString()).isEqualTo("<1.0.0");
    }

    @Test
    void shouldGenerateLessOrEqualExpression() {
        // when
        RangeExpression rangeExpression = lessOrEqual("1.0.0");

        // then
        assertThat(rangeExpression.get().toString()).isEqualTo("<=1.0.0");
    }
}
