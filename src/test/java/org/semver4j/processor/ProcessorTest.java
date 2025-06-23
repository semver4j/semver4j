package org.semver4j.processor;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

@SuppressWarnings("deprecation")
class ProcessorTest {
    @Test
    void nonNullProcessGetsReturned() {
        Processor nonNullResultProcessor = (range, includePrerelease) -> "RESULT";

        assertThat(nonNullResultProcessor.process("RANGE")).isEqualTo("RESULT");
    }

    @Test
    void nullProcessDoesNotGetReturned() {
        Processor nullResultProcessor = (range, includePrerelease) -> null;

        assertThat(nullResultProcessor.process("RANGE")).isEqualTo("RANGE");
    }
}
