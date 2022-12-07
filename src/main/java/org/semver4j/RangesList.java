package org.semver4j;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * <p>Represents set of single {@link Range}'s.</p>
 * It's very convenient way to handle complex ranges string.<br>
 * <br>
 * Following range:
 *
 * <pre>
 * &lt;= 2.6.8 || &gt;= 3.0.0 &lt;= 3.0.1
 * </pre>
 * <p>
 * is parsed into:
 *
 * <pre>
 * RangesList[
 *      rangesList=[
 *          [=2.6.8],
 *          [=3.0.0, =3.0.1]
 *      ]
 * ]
 * </pre>
 * <p>
 * That means, one of ranges {@code [=2.6.8]} or {@code [=3.0.0, =3.0.1]} must by full applied.<br>
 * So that example allows pass following version:
 * <ul>
 *     <li>{@code 2.6.8} - because of range {@code [=2.6.8]}</li>
 *     <li>{@code 3.0.0} - because of range {@code [=3.0.0, =3.0.1]}</li>
 *     <li>{@code 3.0.1} - because of range {@code [=3.0.0, =3.0.1]}</li>
 * </ul>
 * Any other versions not satisfied this range.
 */
public class RangesList {
    private final List<List<Range>> rangesList = new ArrayList<>();

    public RangesList add(List<Range> ranges) {
        if (!ranges.isEmpty()) {
            rangesList.add(ranges);
        }
        return this;
    }

    /**
     * Check whether this ranges list is satisfied by any version.
     *
     * @return {@code true} if this ranges list is satisfied by any version, {@code false} otherwise
     */
    public boolean isSatisfiedByAny() {
        return rangesList.stream()
            .flatMap(List::stream)
            .allMatch(Range::isSatisfiedByAny);
    }

    public boolean isSatisfiedBy(Semver version) {
        return rangesList.stream()
            .anyMatch(ranges -> isSingleSetOfRangesIsSatisfied(ranges, version));
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", RangesList.class.getSimpleName() + "[", "]")
            .add("rangesList=" + rangesList)
            .toString();
    }

    private boolean isSingleSetOfRangesIsSatisfied(List<Range> ranges, Semver version) {
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
