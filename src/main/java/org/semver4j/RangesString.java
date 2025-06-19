package org.semver4j;

import org.jspecify.annotations.NullMarked;
import org.semver4j.internal.range.RangeProcessorPipeline;
import org.semver4j.internal.range.processor.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;
import static org.semver4j.internal.Tokenizers.COMPARATOR;
import static org.semver4j.internal.range.RangeProcessorPipeline.startWith;

@NullMarked
class RangesString {
    private static final Pattern splitterPattern = compile("(\\s*)([<>]?=?)\\s*");
    private static final Pattern comparatorPattern = compile(COMPARATOR);
    private static final RangeProcessorPipeline rangeProcessorPipeline = startWith(new AllVersionsProcessor())
            .addProcessor(new IvyProcessor())
            .addProcessor(new HyphenProcessor())
            .addProcessor(new CaretProcessor())
            .addProcessor(new TildeProcessor())
            .addProcessor(new XRangeProcessor());

    RangesList get(String range, boolean includePrerelease) {
        RangesList rangesList = new RangesList(includePrerelease);
        range = range.trim();
        String[] rangeSections = range.split("\\|\\|");
        for (String rangeSection : rangeSections) {
            rangeSection = stripWhitespacesBetweenRangeOperator(rangeSection);
            rangeSection = applyProcessors(rangeSection, includePrerelease);

            List<Range> ranges = addRanges(rangeSection);
            rangesList.add(ranges);
        }

        return rangesList;
    }

    private static String stripWhitespacesBetweenRangeOperator(final String rangeSection) {
        Matcher matcher = splitterPattern.matcher(rangeSection);
        return matcher.replaceAll("$1$2").trim();
    }

    private static String applyProcessors(final String range, boolean includePrerelease) {
        return rangeProcessorPipeline.process(range, includePrerelease);
    }

    private static List<Range> addRanges(final String range) {
        List<Range> ranges = new ArrayList<>();

        String[] parsedRanges = range.split("\\s+");
        for (String parsedRange : parsedRanges) {
            Matcher matcher = comparatorPattern.matcher(parsedRange);
            if (matcher.matches()) {
                String rangeOperator = matcher.group(1);
                String version = matcher.group(2);

                ranges.add(new Range(version, Range.RangeOperator.value(rangeOperator)));
            }
        }

        return ranges;
    }
}
