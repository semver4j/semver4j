package org.semver4j;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.semver4j.RangesExpression.*;

class RangesExpressionTest {
    @Test
    void shouldHandleRangeWithOneExpression() {
        //given
        RangesExpression rangeExpressions = equal("1.0.0");

        //when
        RangesList rangesList = rangeExpressions.get();

        //then
        assertThat(rangesList.toString()).isEqualTo("=1.0.0");
    }

    @Test
    void shouldComplexRangeExpression() {
        //given
        RangesExpression rangeExpressions = equal("1.0.0")
            .and(equal("2.0.0")
                .and(equal("3.0.0"))
                .or(equal("4.0.0")
                    .and(equal("5.0.0"))
                )
                .or(equal("6.0.0"))
            )
            .and(less("7.0.0"))
            .and(equal("8.0.0"))
            .or(less("9.0.0"));

        //when
        RangesList rangesList = rangeExpressions.get();

        //then
        assertThat(rangesList.toString()).isEqualTo("(=1.0.0 and =2.0.0 and =3.0.0) or (=4.0.0 and =5.0.0) or =6.0.0 or (<7.0.0 and =8.0.0) or <9.0.0");
    }

    @Test
    void shouldGenerateEqualExpression() {
        //when
        RangesExpression rangesExpression = equal("1.0.0");

        //then
        assertThat(rangesExpression.get().toString()).isEqualTo("=1.0.0");
    }

    @Test
    void shouldGenerateGreaterExpression() {
        //when
        RangesExpression rangesExpression = greater("1.0.0");

        //then
        assertThat(rangesExpression.get().toString()).isEqualTo(">1.0.0");
    }

    @Test
    void shouldGenerateGreaterOrEqualExpression() {
        //when
        RangesExpression rangesExpression = greaterOrEqual("1.0.0");

        //then
        assertThat(rangesExpression.get().toString()).isEqualTo(">=1.0.0");
    }

    @Test
    void shouldGenerateLessExpression() {
        //when
        RangesExpression rangesExpression = less("1.0.0");

        //then
        assertThat(rangesExpression.get().toString()).isEqualTo("<1.0.0");
    }

    @Test
    void shouldGenerateLessOrEqualExpression() {
        //when
        RangesExpression rangesExpression = lessOrEqual("1.0.0");

        //then
        assertThat(rangesExpression.get().toString()).isEqualTo("<=1.0.0");
    }
}
