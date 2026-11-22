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

    @Test
    void shouldCompareNumericIdentifiersLargerThanLong() {
        // given: spec-valid numeric identifiers, one above Long.MAX_VALUE
        Semver semver1 = new Semver("1.0.0-9223372036854775808");
        Semver semver2 = new Semver("1.0.0-1");

        // when
        int cmp1 = Comparator.compareTo(semver1, semver2);
        int cmp2 = Comparator.compareTo(semver2, semver1);

        // then: numeric identifiers are compared numerically
        assertThat(cmp1).isPositive();
        assertThat(cmp2).isNegative();
    }

    @Test
    void shouldCompareAlphanumericIdentifiersLexically() {
        assertThat(Comparator.compareTo(new Semver("1.2.3-r2"), new Semver("1.2.3-r100")))
                .isPositive();
        assertThat(Comparator.compareTo(new Semver("1.2.3-r100"), new Semver("1.2.3-r2")))
                .isNegative();
        assertThat(Comparator.compareTo(new Semver("1.2.3-r9223372036854775808"), new Semver("1.2.3-r9")))
                .isPositive();
    }

    @Test
    void shouldOrderNumericIdentifiersBeforeAlphanumericIdentifiers() {
        assertThat(Comparator.compareTo(new Semver("1.2.3-9"), new Semver("1.2.3--a")))
                .isNegative();
    }

    @Test
    void shouldComparePrereleaseLengthsWithoutTreatingIdentifiersAsMissing() {
        assertThat(Comparator.compareTo(new Semver("1.2.3-undef"), new Semver("1.2.3-undef.1")))
                .isNegative();
    }
}
