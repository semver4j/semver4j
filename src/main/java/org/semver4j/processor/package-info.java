/**
 * Provides processors for handling various version range formats in the semver4j library.
 *
 * <p>This package contains implementations of the {@link org.semver4j.processor.Processor} interface that translate
 * different version range notations into a standardized format. Each processor is responsible for handling a specific
 * version range syntax, such as:
 *
 * <ul>
 *   <li>Hyphen ranges (e.g., {@code 1.2.3 - 2.3.4})
 *   <li>Caret ranges (e.g., {@code ^1.2.3})
 *   <li>Tilde ranges (e.g., {@code ~1.2.3})
 *   <li>X-ranges with wildcards (e.g., {@code 1.x.x})
 *   <li>Ivy-style ranges (e.g., {@code [1.0,2.0)})
 *   <li>And more
 * </ul>
 *
 * <p>Processors are designed to work in a chain of responsibility pattern, where each processor attempts to handle the
 * input range string and returns a processed result if successful, or {@code null} to let the next processor try.
 *
 * <p>This package is marked with {@code @NullMarked} to indicate that all types in this package are null-safe by
 * default, with nullable references explicitly marked with {@code @Nullable}.
 *
 * @see org.semver4j.processor.Processor The interface implemented by all processors
 * @see org.semver4j.processor.CompositeProcessor For combining multiple processors
 */
@NullMarked
package org.semver4j.processor;

import org.jspecify.annotations.NullMarked;
