package org.semver4j.internal.range.processor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Processor for pipeline range translations.
 */
public abstract class Processor {
    private boolean includePrerelease = false;

    @Deprecated
    @NotNull
    public String process(@NotNull String range) {
        return Optional.ofNullable(tryProcess(range)).orElse(range);
    };

    @Nullable
    public abstract String tryProcess(@NotNull String range);

    public Processor includePrerelease() {
        this.includePrerelease = true;
        return this;
    }

    boolean getIncludePrerelease() {
        return this.includePrerelease;
    }


}
