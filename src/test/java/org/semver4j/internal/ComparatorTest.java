package org.semver4j.internal;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.semver4j.Semver;

class ComparatorTest {
    @Test
    void shouldUnevenComparisonNotCrashAndBeCorrect() {
        // given
        Semver semver1 = new Semver("4.0.0-beta.9-macro");
        Semver semver2 = new Semver("4.0.0-beta.9-macro2");

        // when
        int cmp1 = Comparator.compareTo(semver1, semver2);
        int cmp2 = Comparator.compareTo(semver2, semver1);

        // then
        assertThat(cmp1).isNegative();
        assertThat(cmp2).isPositive();
    }
}
