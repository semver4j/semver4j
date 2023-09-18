package org.semver4j;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Class for create a {@link RangesList} object.
 */
public class RangesListFactory {
    @NotNull
    public static RangesList create(@NotNull final String range) {
        return new RangesString().get(range);
    }

    /**
     * @since 4.2.0
     */
    @NotNull
    @ApiStatus.AvailableSince("4.2.0")
    public static RangesList create(@NotNull final RangesExpression rangeExpressions) {
        return rangeExpressions.get();
    }
}
