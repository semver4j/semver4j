package org.semver4j.range;

import static java.util.regex.Pattern.compile;
import static org.semver4j.internal.Tokenizers.COMPARATOR;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.semver4j.processor.CompositeProcessor;
import org.semver4j.processor.Processor;

/**
 * Handles semantic version range expressions and converts them into a structured format. This class parses string
 * representations of version ranges and transforms them into {@link RangeList} objects for version comparison
 * operations.
 */
class RangeExpressionParser {
    private static final Pattern SPLITTER_PATTERN = compile("(\\s*)([<>]?=?)\\s*");
    private static final Pattern COMPARATOR_PATTERN = compile(COMPARATOR);

    private final Processor processor;

    /** Constructs a new {@code RangeExpressionParser} with all available processors. */
    RangeExpressionParser() {
        this(CompositeProcessor.all());
    }

    /**
     * Constructs a new {@code RangeExpressionParser} with the specified processor.
     *
     * @param processor the processor to use for range processing
     */
    RangeExpressionParser(Processor processor) {
        this.processor = processor;
    }

    /**
     * Parses a range string and converts it to a structured {@link RangeList}.
     *
     * @param range the version range string to parse
     * @param includePreRelease whether to include pre-release versions in the range
     * @return a structured representation of the version ranges
     */
    RangeList parse(String range, boolean includePreRelease) {
        RangeList rangeList = new RangeList(includePreRelease);
        range = range.trim();
        String[] rangeSections = range.split("\\|\\|");
        for (String rangeSection : rangeSections) {
            rangeSection = stripWhitespacesBetweenRangeOperator(rangeSection);
            rangeSection = applyProcessors(rangeSection, includePreRelease);

            List<Range> ranges = addRanges(rangeSection);
            rangeList.add(ranges);
        }

        return rangeList;
    }

    private static String stripWhitespacesBetweenRangeOperator(String rangeSection) {
        Matcher matcher = SPLITTER_PATTERN.matcher(rangeSection);
        return matcher.replaceAll("$1$2").trim();
    }

    private String applyProcessors(String range, boolean includePreRelease) {
        return Optional.ofNullable(processor.process(range, includePreRelease)).orElse(range);
    }

    private static List<Range> addRanges(String range) {
        List<Range> ranges = new ArrayList<>();

        String[] parsedRanges = range.split("\\s+");
        for (String parsedRange : parsedRanges) {
            Matcher matcher = COMPARATOR_PATTERN.matcher(parsedRange);
            if (matcher.matches()) {
                String rangeOperator = matcher.group(1);
                String version = matcher.group(2);

                ranges.add(new Range(version, Range.RangeOperator.value(rangeOperator)));
            }
        }

        return ranges;
    }
}
