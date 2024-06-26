package org.semver4j.internal.range.processor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("deprecation")
class ProcessorTest {
    @Test
    void nonNullProcessGetsReturned() {
        Processor nonNullResultProcessor = new Processor() {
            @Override
            public @NotNull String tryProcess(@NotNull String range) {
                return "RESULT";
            }
        };

        assertThat(nonNullResultProcessor.process("RANGE")).isEqualTo("RESULT");
    }

    @Test
    void nullProcessDoesNotGetReturned() {
        Processor nullResultProcessor = new Processor() {
            @Override
            public @Nullable String tryProcess(@NotNull String range) {
                return null;
            }
        };

        assertThat(nullResultProcessor.process("RANGE")).isEqualTo("RANGE");
    }
}
