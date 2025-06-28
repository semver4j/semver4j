/**
 * Internal implementation details for the semver4j library.
 *
 * <p>This package contains internal utility classes, parsers, and implementation details that support the public API of
 * the semver4j library. Classes in this package are not intended for direct use by library consumers and do not provide
 * any backward compatibility guarantees between versions.
 *
 * <p>The classes in this package handle the core parsing logic, tokenization, and other low-level operations needed to
 * implement the semantic versioning specification.
 *
 * <p>This package is marked with {@code @NullMarked} to indicate that all types in this package are null-safe by
 * default, with nullable references explicitly marked with {@code @Nullable}.
 *
 * @see org.semver4j The public API package
 */
@NullMarked
package org.semver4j.internal;

import org.jspecify.annotations.NullMarked;
