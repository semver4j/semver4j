package org.semver4j;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.semver4j.Range.RangeOperator;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class RangesListFactoryTest {
    @Test
    void shouldCreateRangesListThatSatisfiesAny() {
        //given
        RangesList rangesList = RangesListFactory.create("*");

        //when/then
        assertThat(rangesList.isSatisfiedByAny()).isTrue();
        assertThat(rangesList.get()).isEqualTo(singletonList(singletonList(new Range(Semver.ZERO, RangeOperator.GTE))));
    }

    @ParameterizedTest
    @ValueSource(strings = {"=1.2", "1.2"})
    void shouldBuildRangeListFromExactVersion(String range) {
        //given
        RangesList rangesList = RangesListFactory.create(range);

        //when
        boolean satisfiedBy = rangesList.isSatisfiedBy(Semver.coerce("1.2"));

        //then
        assertThat(satisfiedBy).isTrue();
    }
}
