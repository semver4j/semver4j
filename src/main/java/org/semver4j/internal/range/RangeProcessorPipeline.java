package org.semver4j.internal.range;

import org.jetbrains.annotations.NotNull;
import org.semver4j.internal.range.processor.Processor;

public class RangeProcessorPipeline {
    @NotNull
    private final Processor currentProcessor;

    public RangeProcessorPipeline(@NotNull final Processor currentProcessor) {
        this.currentProcessor = currentProcessor;
    }

    @NotNull
    public RangeProcessorPipeline addProcessor(@NotNull final Processor processor) {
        return new RangeProcessorPipeline(version -> processor.process(currentProcessor.process(version)));
    }

    @NotNull
    public String process(@NotNull final String range) {
        return currentProcessor.process(range);
    }

    @NotNull
    public static RangeProcessorPipeline startWith(@NotNull final Processor processor) {
        return new RangeProcessorPipeline(processor);
    }
}
