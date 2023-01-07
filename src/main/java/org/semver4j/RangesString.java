package org.semver4j;

import org.semver4j.internal.range.processor.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;
import static org.semver4j.internal.Tokenizers.COMPARATOR;
import static org.semver4j.internal.range.RangeProcessorPipeline.startWith;

class RangesString {
    private static final Pattern splitterPattern = compile("(\\s*)([<>]?=?)\\s*");
    private static final Pattern comparatorPattern = compile(COMPARATOR);

    RangesList get(String range) {
        RangesList rangesList = new RangesList();

        range = range.trim();
        String[] rangeSections = range.split("\\|\\|");
        for (String rangeSection : rangeSections) {
            rangeSection = stripWhitespacesBetweenRangeOperator(rangeSection);
            rangeSection = applyProcessors(rangeSection);

            List<Range> ranges = addRanges(rangeSection);
            rangesList.add(ranges);
        }

        return rangesList;
    }

    private static String stripWhitespacesBetweenRangeOperator(String rangeSection) {
        Matcher matcher = splitterPattern.matcher(rangeSection);
        return matcher.replaceAll("$1$2").trim();
    }

    private static String applyProcessors(String range) {
        return startWith(new GreaterThanOrEqualZeroProcessor())
            .addProcessor(new IvyProcessor())
            .addProcessor(new HyphenProcessor())
            .addProcessor(new CaretProcessor())
            .addProcessor(new TildeProcessor())
            .addProcessor(new XRangeProcessor())
            .process(range);
    }

    private static List<Range> addRanges(String range) {
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
