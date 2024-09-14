package org.semver4j.internal.range.processor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Processor for pipeline range translations.
 */
public interface Processor {
    @Deprecated
    @NotNull
    default String process(@NotNull String range) {
        return Optional.ofNullable(tryProcess(range)).orElse(range);
    };

    @Nullable
    String tryProcess(@NotNull String range);
}
