package org.semver4j;

import org.jetbrains.annotations.NotNull;
import org.semver4j.internal.range.RangeProcessorPipeline;
import org.semver4j.internal.range.processor.AllVersionsProcessor;
import org.semver4j.internal.range.processor.CaretProcessor;
import org.semver4j.internal.range.processor.HyphenProcessor;
import org.semver4j.internal.range.processor.IvyProcessor;
import org.semver4j.internal.range.processor.TildeProcessor;
import org.semver4j.internal.range.processor.XRangeProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;
import static org.semver4j.internal.Tokenizers.COMPARATOR;
import static org.semver4j.internal.range.RangeProcessorPipeline.startWith;

class RangesString {
    @NotNull
    private static final Pattern splitterPattern = compile("(\\s*)([<>]?=?)\\s*");
    @NotNull
    private static final Pattern comparatorPattern = compile(COMPARATOR);
    @NotNull
    private static final RangeProcessorPipeline rangeProcessorPipeline = startWith(new AllVersionsProcessor())
            .addProcessor(new IvyProcessor())
            .addProcessor(new HyphenProcessor())
            .addProcessor(new CaretProcessor())
            .addProcessor(new TildeProcessor())
            .addProcessor(new XRangeProcessor());

    @NotNull
    RangesList get(@NotNull String range) {
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

    @NotNull
    private static String stripWhitespacesBetweenRangeOperator(@NotNull final String rangeSection) {
        Matcher matcher = splitterPattern.matcher(rangeSection);
        return matcher.replaceAll("$1$2").trim();
    }

    @NotNull
    private static String applyProcessors(@NotNull final String range) {
        return rangeProcessorPipeline.process(range);
    }

    @NotNull
    private static List<@NotNull Range> addRanges(@NotNull final String range) {
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
