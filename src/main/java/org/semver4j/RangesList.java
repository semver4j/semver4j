package org.semver4j;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

/**
 * <p>Represents set of single {@link Range}'s.</p>
 * It's very convenient way to handle complex ranges string.<br>
 * <br>
 * Following range:
 *
 * <pre>
 * &lt;=2.6.8 || &gt;=3.0.0 &lt;=3.0.1
 * </pre>
 * <p>
 * is parsed into:
 *
 * <pre>
 * RangesList[
 *      rangesList=[
 *          [&lt;=2.6.8],
 *          [&gt;=3.0.0, &lt;=3.0.1]
 *      ]
 * ]
 * </pre>
 * <p>
 * That means, one of ranges {@code [>=2.6.8]} or {@code [>=3.0.0, <=3.0.1]} must by full applied.<br>
 * So that example allows pass following version:
 * <ul>
 *     <li>{@code 2.6.8} - because of range {@code [>=2.6.8]}</li>
 *     <li>{@code 3.0.0} - because of range {@code [>=3.0.0, <=3.0.1]}</li>
 *     <li>{@code 3.0.1} - because of range {@code [>=3.0.0, <=3.0.1]}</li>
 * </ul>
 * Any other versions <b>not satisfied</b> this range.
 */
public class RangesList {
    @NotNull
    private static final String OR_JOINER = " or ";
    @NotNull
    private static final String AND_JOINER = " and ";

    @NotNull
    private final List<@NotNull List<@NotNull Range>> rangesList = new ArrayList<>();

    /**
     * Add ranges to ranges list.
     */
    @NotNull
    public RangesList add(@NotNull final List<@NotNull Range> ranges) {
        if (!ranges.isEmpty()) {
            rangesList.add(ranges);
        }
        return this;
    }

    /**
     * Return the list of range lists.
     */
    @NotNull
    public List<@NotNull List<@NotNull Range>> get() {
        return rangesList;
    }

    /**
     * Check whether this ranges list is satisfied by any version.
     */
    public boolean isSatisfiedByAny() {
        return rangesList.stream()
            .flatMap(List::stream)
            .allMatch(Range::isSatisfiedByAny);
    }

    /**
     * Check whether this ranges list is satisfied by version.
     */
    public boolean isSatisfiedBy(@NotNull final Semver version) {
        return rangesList.stream()
            .anyMatch(ranges -> isSingleSetOfRangesIsSatisfied(ranges, version));
    }

    @Override
    @NotNull
    public String toString() {
        return rangesList.stream()
            .map(RangesList::formatRanges)
            .collect(joining(OR_JOINER))
            .replaceAll("^\\(([^()]+)\\)$", "$1");
    }

    @NotNull
    private static String formatRanges(@NotNull final List<@NotNull Range> ranges) {
        String representation = ranges.stream()
            .map(Range::toString)
            .collect(joining(AND_JOINER));

        if (ranges.size() < 2) {
            return representation;
        }

        return format(Locale.ROOT, "(%s)", representation);
    }

    private static boolean isSingleSetOfRangesIsSatisfied(@NotNull final List<@NotNull Range> ranges, @NotNull final Semver version) {
        for (Range range : ranges) {
            if (!range.isSatisfiedBy(version)) {
                return false;
            }
        }

        if (!version.getPreRelease().isEmpty()) {
            for (Range range : ranges) {
                Semver rangeSemver = range.getRangeVersion();
                List<String> preRelease = rangeSemver.getPreRelease();
                if (preRelease.size() > 0) {
                    if (version.getMajor() == rangeSemver.getMajor() &&
                        version.getMinor() == rangeSemver.getMinor() &&
                        version.getPatch() == rangeSemver.getPatch()) {
                        return true;
                    }
                }
            }
            return false;
        }

        return true;
    }
}
