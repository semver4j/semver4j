package org.semver4j.internal.range.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.lang.String.join;
import static org.semver4j.Range.RangeOperator.GTE;
import static org.semver4j.Range.RangeOperator.LT;
import static org.semver4j.internal.range.processor.RangesUtils.isX;
import static org.semver4j.internal.range.processor.RangesUtils.parseIntWithXSupport;

public class XRangeProcessor implements Processor {
    private static final String R = "^((?:<|>)?=?)\\s*[v=\\s]*(0|[1-9]\\d*|x|X|\\+|\\*)(?:\\.(0|[1-9]\\d*|x|X|\\+|\\*)(?:\\.(0|[1-9]\\d*|x|X|\\+|\\*)(?:(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][a-zA-Z0-9-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][a-zA-Z0-9-]*))*)))?(?:\\+([0-9A-Za-z-]+(?:\\.[0-9A-Za-z-]+)*))?)?)?$";
    private static final Pattern pattern = Pattern.compile(R);

    @Override
    public String process(String range) {
        String[] rangeVersions = range.split("\\s+");

        List<String> objects = new ArrayList<>();
        for (String rangeVersion : rangeVersions) {
            Matcher matcher = pattern.matcher(rangeVersion);

            if (matcher.matches()) {
                String fullVersion = matcher.group(0);
                String compareSign = matcher.group(1);

                Integer major = parseIntWithXSupport(matcher.group(2));
                Integer minor = parseIntWithXSupport(matcher.group(3));
                Integer patch = parseIntWithXSupport(matcher.group(4));
                String preRelease = matcher.group(5);
                String build = matcher.group(6);

                if (compareSign.equals("=") && isX(patch)) {
                    compareSign = "";
                }

                if (!compareSign.isEmpty() && isX(patch)) {
                    if (isX(minor)) {
                        minor = 0;
                    }
                    patch = 0;
                    if (compareSign.equals(">")) {
                        compareSign = ">=";

                        if (isX(minor)) {
                            major = major + 1;
                            minor = 0;
                            patch = 0;
                        } else {
                            minor = minor + 1;
                            patch = 0;
                        }
                    } else if (compareSign.equals("<=")) {
                        compareSign = "<";
                        if (isX(minor)) {
                            major = major + 1;
                        } else {
                            minor = minor + 1;
                        }
                    }

                    String from = format("%s%d.%d.%d", compareSign, major, minor, patch);
                    objects.add(from);
                } else if (isX(minor)) {
                    String from = format("%s%d.0.0", GTE.asString(), major);
                    String to = format("%s%d.0.0", LT.asString(), (major + 1));
                    objects.add(from);
                    objects.add(to);
                } else if (isX(patch)) {
                    String from = format("%s%d.%d.0", GTE.asString(), major, minor);
                    String to = format("%s%d.%d.0", LT.asString(), major, (minor + 1));
                    objects.add(from);
                    objects.add(to);
                } else {
                    objects.add(fullVersion);
                }
            }
        }

        if (objects.isEmpty()) {
            return range;
        }

        return join(" ", objects);
    }
}
