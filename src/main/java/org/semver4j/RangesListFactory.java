package org.semver4j;

import org.jetbrains.annotations.NotNull;

import static org.jetbrains.annotations.ApiStatus.AvailableSince;

/**
 * Class for create a {@link RangesList} object.
 */
public class RangesListFactory {
    @NotNull
    public static RangesList create(@NotNull final String range) {
        return create(range, false);
    }

    @NotNull
    public static RangesList create(@NotNull final String range, boolean includePrerelease) {
        return new RangesString().get(range, includePrerelease);
    }

    /**
     * @since 4.2.0
     */
    @NotNull
    @AvailableSince("4.2.0")
    public static RangesList create(@NotNull final RangesExpression rangeExpressions) {
        return rangeExpressions.get();
    }
}
