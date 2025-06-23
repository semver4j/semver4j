package org.semver4j.internal;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.semver4j.Semver;

class ComparatorTest {
    @Test
    void shouldUnevenComparisonNotCrashAndBeCorrect() {
        Semver semver1 = Semver.coerce("4.0.0-beta.9-macro");
        Semver semver2 = Semver.coerce("4.0.0-beta.9-macro2");

        assertThat(semver1).isNotNull();
        assertThat(semver2).isNotNull();

        assertThat(Comparator.compareTo(semver1, semver2)).isNegative();
        assertThat(Comparator.compareTo(semver2, semver1)).isPositive();
    }
}
