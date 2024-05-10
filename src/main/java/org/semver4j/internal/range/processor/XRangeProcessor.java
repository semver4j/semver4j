package org.semver4j.internal.range.processor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.regex.Pattern.compile;
import static org.semver4j.Range.RangeOperator.EQ;
import static org.semver4j.Range.RangeOperator.GT;
import static org.semver4j.Range.RangeOperator.GTE;
import static org.semver4j.Range.RangeOperator.LT;
import static org.semver4j.Range.RangeOperator.LTE;
import static org.semver4j.internal.Tokenizers.XRANGE;
import static org.semver4j.internal.range.processor.RangesUtils.EMPTY;
import static org.semver4j.internal.range.processor.RangesUtils.SPACE;
import static org.semver4j.internal.range.processor.RangesUtils.isX;
import static org.semver4j.internal.range.processor.RangesUtils.parseIntWithXSupport;

/**
 * <p>Processor for translate <a href="https://github.com/npm/node-semver#x-ranges-12x-1x-12-">X-Ranges</a> into classic
 * range.</p>
 * <br>
 */
public class XRangeProcessor implements Processor {
    @NotNull
    private static final Pattern pattern = compile(XRANGE);

    @Override
    public @Nullable String tryProcess(@NotNull String range) {
        String[] rangeVersions = range.split("\\s+");

        List<String> objects = new ArrayList<>();
        for (String rangeVersion : rangeVersions) {
            Matcher matcher = pattern.matcher(rangeVersion);

            if (matcher.matches()) {
                String fullRange = matcher.group(0);

                String compareSign = matcher.group(1);

                int major = parseIntWithXSupport(matcher.group(2));
                int minor = parseIntWithXSupport(matcher.group(3));
                int patch = parseIntWithXSupport(matcher.group(4));

                if (compareSign.equals(EQ.asString()) && isX(patch)) {
                    compareSign = EMPTY;
                }

                if (!compareSign.isEmpty() && isX(patch)) {
                    patch = 0;
                    if (compareSign.equals(GT.asString())) {
                        compareSign = GTE.asString();

                        if (isX(minor)) {
                            major = major + 1;
                            minor = 0;
                        } else {
                            minor = minor + 1;
                        }
                    } else if (compareSign.equals(LTE.asString())) {
                        compareSign = LT.asString();
                        if (isX(minor)) {
                            major = major + 1;
                            minor = 0;
                        } else {
                            minor = minor + 1;
                        }
                    } else if (isX(minor)) {
                        minor = 0;
                    }

                    String from = format(Locale.ROOT, "%s%d.%d.%d", compareSign, major, minor, patch);
                    objects.add(from);
                } else if (isX(minor)) {
                    String from = format(Locale.ROOT, "%s%d.0.0", GTE.asString(), major);
                    String to = format(Locale.ROOT, "%s%d.0.0", LT.asString(), (major + 1));
                    objects.add(from);
                    objects.add(to);
                } else if (isX(patch)) {
                    String from = format(Locale.ROOT, "%s%d.%d.0", GTE.asString(), major, minor);
                    String to = format(Locale.ROOT, "%s%d.%d.0", LT.asString(), major, (minor + 1));
                    objects.add(from);
                    objects.add(to);
                } else {
                    objects.add(fullRange);
                }
            }
        }

        if (objects.isEmpty()) {
            return null;
        }

        return join(SPACE, objects);
    }
}
