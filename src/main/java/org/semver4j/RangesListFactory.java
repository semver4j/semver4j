package org.semver4j;

import org.semver4j.Range.RangeOperator;
import org.semver4j.internal.range.processor.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;
import static org.semver4j.internal.Tokenizers.COMPARATOR;
import static org.semver4j.internal.range.RangeProcessorPipeline.startWith;

public class RangesListFactory {
    private static final Pattern pattern = compile(COMPARATOR);

    public static RangesList create(String range) {
        range = range.trim();
        RangesList rangesList = new RangesList();

        Pattern compile = compile("(\\s*)((?:<|>)?=?)\\s*");

        String[] rangeSections = range.split("\\|\\|");
        for (String rangeSection : rangeSections) {
            rangeSection = stripWhitespacesBetweenRangeOperator(compile, rangeSection);
            rangeSection = applyProcessors(rangeSection);

            List<Range> ranges = addRanges(rangeSection);
            rangesList.add(ranges);
        }

        return rangesList;
    }

    private static String stripWhitespacesBetweenRangeOperator(Pattern compile, String rangeSection) {
        Matcher matcher = compile.matcher(rangeSection);
        return matcher.replaceAll("$1$2");
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
            Matcher matcher = pattern.matcher(parsedRange);
            if (matcher.matches()) {
                String rangeOperator = matcher.group(1);
                String version = matcher.group(2);

                ranges.add(new Range(version, RangeOperator.value(rangeOperator)));
            }
        }

        return ranges;
    }
}
