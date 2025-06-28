package org.semver4j.range;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.semver4j.Semver;

/**
 * Represents a set of version range constraints that can be used to match semantic versions.
 *
 * <p>A {@code RangeList} contains multiple sets of {@link Range} objects. A version satisfies the {@code RangeList} if
 * it satisfies at least one complete set of ranges within the list.
 *
 * <p>For example, the range expression:
 *
 * <pre>{@code
 * <=2.6.8 || >=3.0.0 <=3.0.1
 * }</pre>
 *
 * <p>is parsed into:
 *
 * <pre>{@code
 * RangeList[
 *      rangesList=[
 *          [<=2.6.8],
 *          [>=3.0.0, <=3.0.1]
 *      ]
 * ]
 * }</pre>
 *
 * <p>This means a version must satisfy either {@code <=2.6.8} OR both {@code >=3.0.0} AND {@code <=3.0.1}.
 *
 * <p>Examples of versions that satisfy this range:
 *
 * <ul>
 *   <li>{@code 2.6.8} - satisfies the first range set {@code [<=2.6.8]}
 *   <li>{@code 2.0.0} - satisfies the first range set {@code [<=2.6.8]}
 *   <li>{@code 3.0.0} - satisfies the second range set {@code [>=3.0.0, <=3.0.1]}
 *   <li>{@code 3.0.1} - satisfies the second range set {@code [>=3.0.0, <=3.0.1]}
 * </ul>
 *
 * <p>Versions that do NOT satisfy this range include:
 *
 * <ul>
 *   <li>{@code 2.7.0} - greater than 2.6.8 but less than 3.0.0
 *   <li>{@code 3.0.2} - greater than 3.0.1
 * </ul>
 *
 * @see Range The individual constraint used within range sets
 * @see RangeListFactory Factory methods for creating RangeList instances
 * @see Semver#satisfies(RangeList) Method to check if a version satisfies a range
 */
public class RangeList {
    private static final String OR_JOINER = " or ";
    private static final String AND_JOINER = " and ";

    private final List<List<Range>> rangesList = new ArrayList<>();

    private final boolean includePreRelease;

    /**
     * Constructs a new {@code RangeList} with the specified {@code pre-release} inclusion behavior.
     *
     * @param includePreRelease if {@code true}, {@code pre-release} versions will be included in range matching; if
     *     {@code false}, {@code pre-release} versions will only match when explicitly specified
     */
    public RangeList(boolean includePreRelease) {
        this.includePreRelease = includePreRelease;
    }

    /**
     * Adds a set of ranges to this range list.
     *
     * <p>Each set of ranges represents a complete condition that, if satisfied, means the entire range list is
     * satisfied. Multiple sets of ranges are connected with logical {@code OR} operations.
     *
     * @param ranges the set of ranges to add (connected with logical {@code AND})
     * @return this {@code RangeList} instance for method chaining
     */
    public RangeList add(final List<Range> ranges) {
        if (!ranges.isEmpty()) {
            rangesList.add(ranges);
        }
        return this;
    }

    /**
     * Returns the list of range sets contained in this range list.
     *
     * @return the internal list of range sets
     */
    public List<List<Range>> get() {
        return rangesList;
    }

    /**
     * Checks if this range list can be satisfied by any version.
     *
     * <p>A range list is satisfiable if all individual ranges in all sets can be satisfied by at least one version.
     *
     * @return {@code true} if at least one version could potentially satisfy this range list, {@code false} if no
     *     version can satisfy it
     */
    public boolean isSatisfiedByAny() {
        return rangesList.stream().flatMap(List::stream).allMatch(Range::isSatisfiedByAny);
    }

    /**
     * Checks if the specified version satisfies this range list.
     *
     * <p>A version satisfies the range list if it satisfies at least one complete set of ranges.
     *
     * @param version the semantic version to check
     * @return {@code true} if the version satisfies at least one set of ranges in this list, {@code false} otherwise
     */
    public boolean isSatisfiedBy(Semver version) {
        return rangesList.stream().anyMatch(ranges -> isSingleSetOfRangesIsSatisfied(ranges, version));
    }

    /**
     * Returns a string representation of this range list.
     *
     * <p>Individual range sets are joined with {@code " or "}, and ranges within each set are joined with {@code " and
     * "}. Sets with multiple ranges are enclosed in parentheses for clarity.
     *
     * @return a string representation of this range list
     */
    @Override
    public String toString() {
        return rangesList.stream()
                .map(RangeList::formatRanges)
                .collect(joining(OR_JOINER))
                .replaceAll("^\\(([^()]+)\\)$", "$1");
    }

    private static String formatRanges(List<Range> ranges) {
        String representation = ranges.stream().map(Range::toString).collect(joining(AND_JOINER));

        if (ranges.size() < 2) {
            return representation;
        }

        return format(Locale.ROOT, "(%s)", representation);
    }

    private boolean isSingleSetOfRangesIsSatisfied(List<Range> ranges, Semver version) {
        for (Range range : ranges) {
            if (!range.isSatisfiedBy(version)) {
                return false;
            }
        }

        if (!version.getPreRelease().isEmpty() && !includePreRelease) {
            for (Range range : ranges) {
                Semver rangeSemver = range.getRangeVersion();
                List<String> prerelease = rangeSemver.getPreRelease();
                if (!prerelease.isEmpty()
                        && version.getMajor() == rangeSemver.getMajor()
                        && version.getMinor() == rangeSemver.getMinor()
                        && version.getPatch() == rangeSemver.getPatch()) {
                    return true;
                }
            }
            return false;
        }

        return true;
    }
}
