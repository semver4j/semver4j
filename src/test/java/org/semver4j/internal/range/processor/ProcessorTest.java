package org.semver4j.internal.range.processor;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("deprecation")
class ProcessorTest {
    @Test
    void nonNullProcessGetsReturned() {
        Processor nonNullResultProcessor = new Processor() {
            @Override
            @Nullable
            public String tryProcess(String range) {
                return "RESULT";
            }
        };

        assertThat(nonNullResultProcessor.process("RANGE")).isEqualTo("RESULT");
    }

    @Test
    void nullProcessDoesNotGetReturned() {
        Processor nullResultProcessor = new Processor() {
            @Override
            @Nullable
            public String tryProcess(String range) {
                return null;
            }
        };

        assertThat(nullResultProcessor.process("RANGE")).isEqualTo("RANGE");
    }
}
