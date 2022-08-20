package org.semver4j.internal.range.processor;

/**
 * Processor for pipeline range translations.
 */
public interface Processor {
    String process(String range);
}
