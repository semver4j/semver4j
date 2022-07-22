package org.semver4j.internal.range;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.semver4j.RangesList;
import org.semver4j.Semver;

import static org.assertj.core.api.Assertions.assertThat;

class RangesListFactoryTest {
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
