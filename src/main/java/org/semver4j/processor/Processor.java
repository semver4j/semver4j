package org.semver4j.processor;

import org.jspecify.annotations.Nullable;

/** Processor for pipeline range translations. */
public interface Processor {
    String LOWEST_PRERELEASE = "-0";

    @Nullable
    String process(String range, boolean includePreRelease);
}
