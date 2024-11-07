package org.semver4j.internal.range.processor;

import java.util.Optional;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Processor for pipeline range translations.
 */
@NullMarked
public abstract class Processor {
    private boolean includePrerelease;

    @Deprecated
    public String process(String range) {
        return Optional.ofNullable(tryProcess(range)).orElse(range);
    }

    @Nullable
    public abstract String tryProcess(String range);

    public Processor includePrerelease() {
        this.includePrerelease = true;
        return this;
    }

    boolean getIncludePrerelease() {
        return this.includePrerelease;
    }


}
