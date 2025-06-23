package org.semver4j;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.semver4j.Range.RangeOperator;
import org.semver4j.processor.IvyProcessor;
import org.semver4j.processor.XRangeProcessor;

class RangesListFactoryTest {
    @Test
    void shouldCreateRangesListThatSatisfiesAny() {
        // given
        RangesList rangesList = RangesListFactory.create("*");

        // when/then
        assertThat(rangesList.isSatisfiedByAny()).isTrue();
        assertThat(rangesList.get()).isEqualTo(singletonList(singletonList(new Range(Semver.ZERO, RangeOperator.GTE))));
    }

    @ParameterizedTest
    @ValueSource(strings = {"=1.2", "1.2"})
    void shouldBuildRangeListFromExactVersion(String range) {
        // given
        RangesList rangesList = RangesListFactory.create(range);

        // when
        boolean satisfiedBy = rangesList.isSatisfiedBy(Semver.coerce("1.2"));

        // then
        assertThat(satisfiedBy).isTrue();
    }

    @Test
    void shouldStripWhitespacesBetweenRangeOperator() {
        // given
        RangesList rangesList = RangesListFactory.create("<=   2.6.8 ||    >= 3.0.0 <= 3.0.1 || >5.0.0");

        // when
        String range = rangesList.toString();

        // then
        assertThat(range).isEqualTo("<=2.6.8 or (>=3.0.0 and <=3.0.1) or >5.0.0");
    }

    @Test
    void shouldCorrectParseCaretRangesWithSpace() {
        // given
        RangesList rangesList = RangesListFactory.create("^14.14.20 || ^16.0.0");

        // when
        String range = rangesList.toString();

        // then
        assertThat(range).isEqualTo("(>=14.14.20 and <15.0.0) or (>=16.0.0 and <17.0.0)");
    }

    @Test
    void shouldAllowToConfigureProcessors() {
        // given
        String ivyRange = "[1.0.0,2.0.0]";

        // when
        RangesList ivyRangesList = RangesListFactory.create(ivyRange, new IvyProcessor());
        RangesList nonIvyRangesList = RangesListFactory.create(ivyRange, new XRangeProcessor());

        // then
        assertThat(ivyRangesList.toString()).isEqualTo(">=1.0.0 and <=2.0.0");
        assertThat(nonIvyRangesList.get()).isEmpty();
    }
}
