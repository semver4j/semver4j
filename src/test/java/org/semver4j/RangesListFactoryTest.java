package org.semver4j;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class RangesListFactoryTest {
    @Test
    void shouldCheckSatisfiedByAny() {
        //given
        RangesList rangesList = RangesListFactory.create("*");

        //when/then
        assertThat(rangesList.isSatisfiedByAny()).isTrue();
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
