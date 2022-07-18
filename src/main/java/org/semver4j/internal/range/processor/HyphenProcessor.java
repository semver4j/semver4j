package org.semver4j.internal.range.processor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static org.semver4j.Range.RangeOperator.*;
import static org.semver4j.internal.range.processor.RangesUtils.isX;
import static org.semver4j.internal.range.processor.RangesUtils.parseIntWithXSupport;

public class HyphenProcessor implements Processor {
    private static final String R = "^\\s*([v=\\s]*(0|[1-9]\\d*|x|X|\\*)(?:\\.(0|[1-9]\\d*|x|X|\\*)(?:\\.(0|[1-9]\\d*|x|X|\\*)(?:(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][a-zA-Z0-9-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][a-zA-Z0-9-]*))*)))?(?:\\+([0-9A-Za-z-]+(?:\\.[0-9A-Za-z-]+)*))?)?)?)\\s+-\\s+([v=\\s]*(0|[1-9]\\d*|x|X|\\*)(?:\\.(0|[1-9]\\d*|x|X|\\*)(?:\\.(0|[1-9]\\d*|x|X|\\*)(?:(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][a-zA-Z0-9-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][a-zA-Z0-9-]*))*)))?(?:\\+([0-9A-Za-z-]+(?:\\.[0-9A-Za-z-]+)*))?)?)?)\\s*$";
    private static final Pattern pattern = Pattern.compile(R);

    @Override
    public String process(String range) {
        Matcher matcher = pattern.matcher(range);

        if (matcher.matches()) {
            String rangeFrom = getRangeFrom(matcher);
            String rangeTo = getRangeTo(matcher);

            return format("%s %s", rangeFrom, rangeTo);
        }

        return range;
    }

    private String getRangeFrom(Matcher matcher) {
        String from = matcher.group(1);
        Integer fromMajor = parseIntWithXSupport(matcher.group(2));
        Integer fromMinor = parseIntWithXSupport(matcher.group(3));
        Integer fromPath = parseIntWithXSupport(matcher.group(4));
        String fromPreRelease = matcher.group(5);
        String fromBuild = matcher.group(6);

        if (isX(fromMinor)) {
            return format("%s%d.0.0", GTE.asString(), fromMajor);
        } else if (isX(fromPath)) {
            return format("%s%d.%s.0", GTE.asString(), fromMajor, fromMinor);
        } else {
            return format("%s%s", GTE.asString(), from);
        }
    }

    private String getRangeTo(Matcher matcher) {
        String to = matcher.group(7);
        Integer toMajor = parseIntWithXSupport(matcher.group(8));
        Integer toMinor = parseIntWithXSupport(matcher.group(9));
        Integer toPath = parseIntWithXSupport(matcher.group(10));
        String toPreRelease = matcher.group(11);
        String toBuild = matcher.group(12);

        if (isX(toMinor)) {
            return format("%s%d.0.0", LT.asString(), (toMajor + 1));
        } else if (isX(toPath)) {
            return format("%s%d.%d.0", LT.asString(), toMajor, (toMinor + 1));
        } else {
            return format("%s%s", LTE.asString(), to);
        }
    }
}
