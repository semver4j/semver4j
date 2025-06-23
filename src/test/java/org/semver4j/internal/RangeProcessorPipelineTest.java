package org.semver4j.internal;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.semver4j.processor.Processor;

class RangeProcessorPipelineTest {
    @Test
    void shouldBeAbleToBuildAPipeline() {
        RangeProcessorPipeline pipeline = RangeProcessorPipeline.startWith(new DummyProcessor(range -> range + "_A"))
                .addProcessor(new DummyProcessor(range -> range + "_B"))
                .addProcessor(new DummyProcessor(range -> range + "_C"));
        assertThat(pipeline.process("RANGE", false)).isEqualTo("RANGE_A");
    }

    @Test
    void shouldReturnPassedInStringIfNoProcessorIsSuccessful() {
        RangeProcessorPipeline pipeline = RangeProcessorPipeline.startWith(new DummyProcessor(range -> null))
                .addProcessor(new DummyProcessor(range -> null))
                .addProcessor(new DummyProcessor(range -> null));
        assertThat(pipeline.process("RANGE", false)).isEqualTo("RANGE");
    }

    private static final class DummyProcessor implements Processor {
        private final Function<String, String> process;

        private DummyProcessor(Function<String, String> process) {
            this.process = process;
        }

        @Override
        public String process(String range, boolean includePrerelease) {
            return process.apply(range);
        }
    }
}
