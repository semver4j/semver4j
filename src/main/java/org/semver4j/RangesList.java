package org.semver4j;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class RangesList {
    private final List<List<Range>> rangesList = new ArrayList<>();

    public RangesList add(List<Range> ranges) {
        if (!ranges.isEmpty()) {
            rangesList.add(ranges);
        }
        return this;
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
