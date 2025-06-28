package org.semver4j.range;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RangeListTest {
    @Test
    void shouldHaveHumanReadableToString() {
        // given
        RangeList rangeList = RangeListFactory.create("<=2.6.8 || >=3.0.0 <=3.0.1");

        // when
        String string = rangeList.toString();

        // then
        assertThat(string).isEqualTo("<=2.6.8 or (>=3.0.0 and <=3.0.1)");
    }

    @Test
    void shouldOmitOuterParentheses() {
        // given
        RangeList rangeList = RangeListFactory.create(">=3.0.0 <=3.0.1");

        // when
        String string = rangeList.toString();

        // then
        assertThat(string).isEqualTo(">=3.0.0 and <=3.0.1");
    }
}
