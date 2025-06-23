package org.semver4j.processor;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

public class CompositeProcessor implements Processor {
    private final List<Processor> processors;

    public CompositeProcessor(List<Processor> processors) {
        this.processors = requireNonNull(processors, "processors cannot be null");
    }

    public static Processor all() {
        return new CompositeProcessor(Processors.ALL_PROCESSORS);
    }

    public static Processor of(Processor... processors) {
        return new CompositeProcessor(List.of(processors));
    }

    @Override
    public @Nullable String process(String range, boolean includePreRelease) {
        return processors.stream()
                .map(processor -> processor.process(range, includePreRelease))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}
