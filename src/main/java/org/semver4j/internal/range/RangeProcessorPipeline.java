package org.semver4j.internal.range;

import org.jetbrains.annotations.NotNull;
import org.semver4j.internal.range.processor.Processor;

import java.util.ArrayList;

public class RangeProcessorPipeline {
    @NotNull
    private final ArrayList<@NotNull Processor> processors = new ArrayList<>();

    public RangeProcessorPipeline(@NotNull final Processor currentProcessor) {
        this.processors.add(currentProcessor);
    }

    @NotNull
    public RangeProcessorPipeline addProcessor(@NotNull final Processor processor) {
        processors.add(processor);
        return this;
    }

    @NotNull
    public String process(@NotNull final String range) {
        String processedRange = range;
        for (Processor processor : processors) {
            processedRange = processor.process(processedRange);
        }
        return processedRange;
    }

    @NotNull
    public static RangeProcessorPipeline startWith(@NotNull final Processor processor) {
        return new RangeProcessorPipeline(processor);
    }
}
