package org.semver4j.processor;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

/**
 * A processor that combines multiple processors into a single processing pipeline.
 *
 * <p>This processor delegates the processing to a list of processors and returns the first non-null result. If all
 * processors return null, this processor also returns null.
 *
 * @see Processor
 * @see Processors
 * @since 6.0.0
 */
public class CompositeProcessor implements Processor {
    private final List<Processor> processors;

    private CompositeProcessor(List<Processor> processors) {
        this.processors = requireNonNull(processors, "processors cannot be null");
    }

    /**
     * Creates a composite processor containing all available processors.
     *
     * @return a composite processor with all standard processors
     */
    public static Processor all() {
        return new CompositeProcessor(Processors.ALL_PROCESSORS);
    }

    /**
     * Creates a composite processor from the specified processors.
     *
     * @param processors the processors to include in this composite
     * @return a new composite processor
     */
    public static Processor of(Processor... processors) {
        return new CompositeProcessor(List.of(processors));
    }

    /**
     * Processes the range by trying each processor in sequence until one returns a non-null result.
     *
     * @param range the version range string to process
     * @param includePreRelease whether to include pre-release versions in the range
     * @return the processed range string, or null if no processor could handle the input
     */
    @Override
    public @Nullable String process(String range, boolean includePreRelease) {
        return processors.stream()
                .map(processor -> processor.process(range, includePreRelease))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}
