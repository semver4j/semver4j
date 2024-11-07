package org.semver4j.internal.range;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.semver4j.internal.range.processor.Processor;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class RangeProcessorPipelineTest {
    @Test
    void shouldBeAbleToBuildAPipeline() {
        RangeProcessorPipeline pipeline = RangeProcessorPipeline.startWith(new DummyProcessor(range -> range + "_A"))
                .addProcessor(new DummyProcessor(range -> range + "_B"))
                .addProcessor(new DummyProcessor(range -> range + "_C"));
        assertThat(pipeline.process("RANGE")).isEqualTo("RANGE_A");
    }

    @Test
    void shouldReturnPassedInStringIfNoProcessorIsSuccessful() {
        RangeProcessorPipeline pipeline = RangeProcessorPipeline.startWith(new DummyProcessor(range -> null))
                .addProcessor(new DummyProcessor(range -> null))
                .addProcessor(new DummyProcessor(range -> null));
        assertThat(pipeline.process("RANGE")).isEqualTo("RANGE");
    }

    private final class DummyProcessor extends Processor {
        private final Function<String, String> process;
        private DummyProcessor(Function<String, String> process) {
            this.process = process;
        }

        @Override
        public @Nullable String tryProcess(@NotNull String range) {
            return process.apply(range);
        }
    }
}
