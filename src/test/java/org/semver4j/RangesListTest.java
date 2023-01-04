package org.semver4j;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RangesListTest {
    @Test
    void shouldHaveHumanReadableToString() {
        //given
        RangesList rangesList = RangesListFactory.create("<=2.6.8 || >=3.0.0 <=3.0.1");

        //when/then
        assertThat(rangesList.toString()).isEqualTo("<=2.6.8 or (>=3.0.0 and <=3.0.1)");
    }

    @Test
    void shouldOmitOuterParentheses() {
        //given
        RangesList rangesList = RangesListFactory.create(">=3.0.0 <=3.0.1");

        //when/then
        assertThat(rangesList.toString()).isEqualTo(">=3.0.0 and <=3.0.1");
    }
}
