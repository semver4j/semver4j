package org.semver4j.internal.range.processor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static org.semver4j.Range.RangeOperator.GTE;
import static org.semver4j.Range.RangeOperator.LT;
import static org.semver4j.internal.range.processor.RangesUtils.*;

public class TildeProcessor implements Processor {
    private static final String R = "^(?:~>?)[v=\\s]*(0|[1-9]\\d*|x|X|\\*)(?:\\.(0|[1-9]\\d*|x|X|\\*)(?:\\.(0|[1-9]\\d*|x|X|\\*)(?:(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][a-zA-Z0-9-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][a-zA-Z0-9-]*))*)))?(?:\\+([0-9A-Za-z-]+(?:\\.[0-9A-Za-z-]+)*))?)?)?$";
    private static final Pattern pattern = Pattern.compile(R);

    @Override
    public String process(String range) {
        Matcher matcher = pattern.matcher(range);

        if (matcher.matches()) {
            String fullVersion = matcher.group(0);

            Integer major = parseIntWithXSupport(matcher.group(1));
            Integer minor = parseIntWithXSupport(matcher.group(2));
            Integer path = parseIntWithXSupport(matcher.group(3));
            String preRelease = matcher.group(4);
            String build = matcher.group(5);

            String from;
            String to;

            if (isX(minor)) {
                from = format("%s%d.0.0", GTE.asString(), major);
                to = format("%s%d.0.0", LT.asString(), (major + 1));
            } else if (isX(path)) {
                from = format("%s%d.%d.0", GTE.asString(), major, minor);
                to = format("%s%d.%d.0", LT.asString(), major, (minor + 1));
            } else if (isNotBlank(preRelease)) {
                from = format("%s%d.%d.%d-%s", GTE.asString(), major, minor, path, preRelease);
                to = format("%s%d.%d.0", LT.asString(), major, (minor + 1));
            } else {
                from = format("%s%d.%d.%d", GTE.asString(), major, minor, path);
                to = format("%s%d.%d.0", LT.asString(), major, (minor + 1));
            }

            return format("%s %s", from, to);
        }

        return range;
    }
}
