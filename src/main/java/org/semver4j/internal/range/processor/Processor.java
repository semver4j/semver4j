package org.semver4j.internal.range.processor;

import org.jetbrains.annotations.NotNull;

/**
 * Processor for pipeline range translations.
 */
public interface Processor {
    @NotNull
    String process(@NotNull String range);
}
