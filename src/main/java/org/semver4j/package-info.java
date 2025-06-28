/**
 * The {@code org.semver4j} package provides a comprehensive implementation of the Semantic Versioning specification.
 *
 * <p>This package offers tools for parsing, comparing, and manipulating semantic version strings, as well as working
 * with version ranges and various version range formats. It follows the <a href="https://semver.org/">Semantic
 * Versioning 2.0.0</a> specification.
 *
 * <p>Key features include:
 *
 * <ul>
 *   <li>Parsing and validating semantic version strings
 *   <li>Comparing versions according to precedence rules
 *   <li>Working with pre-release identifiers and build metadata
 *   <li>Support for various version range formats (npm, Ivy, Maven, etc.)
 *   <li>Fluent API for creating and evaluating version constraints
 * </ul>
 *
 * <p>This package uses the {@code @NullMarked} annotation from JSpecify to indicate that all types in this package are
 * null-safe by default, with nullable references explicitly marked with {@code @Nullable}.
 *
 * @see org.semver4j.Semver The main class for working with semantic versions
 * @see org.semver4j.range.Range For individual version constraints
 * @see org.semver4j.range.RangeList For handling complex version range expressions
 * @see org.semver4j.processor For version range format processors
 */
@NullMarked
package org.semver4j;

import org.jspecify.annotations.NullMarked;
