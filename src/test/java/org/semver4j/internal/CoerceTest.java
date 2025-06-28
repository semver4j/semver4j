package org.semver4j.internal;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CoerceTest {
    @Test
    void shouldRemoveLeadingZeros() {
        // when
        String version = Coerce.coerce("1.08");

        // then
        assertThat(version).isEqualTo("1.8.0");
    }
}
