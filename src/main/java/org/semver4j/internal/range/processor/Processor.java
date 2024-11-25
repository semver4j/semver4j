package org.semver4j.internal.range.processor;

import java.util.Optional;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Processor for pipeline range translations.
 */
@NullMarked
public interface Processor {
    @Deprecated
    default String process(String range) {
        return Optional.ofNullable(tryProcess(range)).orElse(range);
    }

    @Nullable
    String tryProcess(String range);
}
