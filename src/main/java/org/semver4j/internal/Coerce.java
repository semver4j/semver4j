package org.semver4j.internal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.regex.Pattern.compile;

public class Coerce {
    @NotNull
    private static final Pattern PATTERN = compile(
        "(^|\\D)(\\d{1,16})(?:\\.(\\d{1,16}))?(?:\\.(\\d{1,16}))?(?:$|\\D)"
    );

    private Coerce() {
    }

    @Nullable
    public static String coerce(@NotNull final String version) {
        Matcher matcher = PATTERN.matcher(version);

        if (matcher.find()) {
            String coercedMajor = matcher.group(2);
            String coercedMinor = Optional.ofNullable(matcher.group(3)).orElse("0");
            String coercedPath = Optional.ofNullable(matcher.group(4)).orElse("0");

            return format(Locale.ROOT, "%s.%s.%s", coercedMajor, coercedMinor, coercedPath);
        }

        return null;
    }
}
