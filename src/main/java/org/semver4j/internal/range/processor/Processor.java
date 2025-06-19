package org.semver4j.internal.range.processor;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * Processor for pipeline range translations.
 */
@NullMarked
public interface Processor {
    String LOWEST_PRERELEASE = "-0";

    @Deprecated
    default String process(String range) {
        return Optional.ofNullable(process(range, false)).orElse(range);
    }

    @Nullable
    String process(String range, boolean includePrerelease);
}
