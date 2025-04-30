package org.semver4j.internal;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CoerceTest {
    @Test
    void shouldRemoveLeadingZeros() {
        String version = Coerce.coerce("1.08");

        assertThat(version).isEqualTo("1.8.0");
    }
}
