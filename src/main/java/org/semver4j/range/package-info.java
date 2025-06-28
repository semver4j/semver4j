/**
 * Provides classes for working with semantic version range expressions.
 *
 * <p>This package contains the core components for defining, parsing, and evaluating version range expressions that can
 * be used to match specific semantic versions. The version ranges allow specifying constraints like "greater than
 * version X", "compatible with version Y", or complex combinations of multiple constraints.
 *
 * <p>Key components in this package include:
 *
 * <ul>
 *   <li>{@link org.semver4j.range.Range} - Represents a single version constraint with an operator
 *   <li>{@link org.semver4j.range.RangeList} - Represents a set of version constraints combined with logical operators
 *   <li>{@link org.semver4j.range.RangeExpression} - Provides a fluent API for building version range expressions
 *   <li>{@link org.semver4j.range.RangeListFactory} - Creates range lists from string expressions
 * </ul>
 *
 * <p>Version ranges can be specified using various formats, including:
 *
 * <ul>
 *   <li>Simple comparisons: {@code >1.2.3}, {@code >=2.0.0}, {@code <3.0.0}
 *   <li>Exact version match: {@code =1.2.3}
 *   <li>Logical combinations: {@code >=1.0.0 <2.0.0} (AND), {@code >=1.0.0 || >=3.0.0 <4.0.0} (OR)
 * </ul>
 *
 * <p>This package is marked with {@code @NullMarked} to indicate that all types in this package are null-safe by
 * default, with nullable references explicitly marked with {@code @Nullable}.
 *
 * @see org.semver4j.Semver#satisfies(org.semver4j.range.RangeList) For checking if a version satisfies a range
 */
@NullMarked
package org.semver4j.range;

import org.jspecify.annotations.NullMarked;
