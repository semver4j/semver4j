package org.semver4j.internal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.semver4j.SemverException;

import java.math.BigInteger;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.regex.Pattern.compile;
import static org.semver4j.internal.Tokenizers.STRICT;

public class StrictParser {
    @NotNull
    private static final Pattern pattern = compile(STRICT);
    @NotNull
    private static final BigInteger maxInt = BigInteger.valueOf(Integer.MAX_VALUE);

    private StrictParser() {
    }

    @NotNull
    public static Version parse(@NotNull final String version) {
        Matcher matcher = pattern.matcher(version);

        if (!matcher.matches()) {
            throw new SemverException(format(Locale.ROOT, "Version [%s] is not valid semver.", version));
        }

        int major = parseInt(matcher.group(1));
        int minor = parseInt(matcher.group(2));
        int patch = parseInt(matcher.group(3));
        List<String> preRelease = convertToList(matcher.group(4));
        List<String> build = convertToList(matcher.group(5));

        return new Version(major, minor, patch, preRelease, build);
    }

    private static int parseInt(@NotNull final String maybeInt) {
        BigInteger secureNumber = new BigInteger(maybeInt);
        if (maxInt.compareTo(secureNumber) < 0) {
            throw new SemverException(format(Locale.ROOT, "Value [%s] is too big.", maybeInt));
        }
        /*
         * Do not use BigInteger.intValueExact() because it is not available on Android with API < 31.
         * The use of BigInteger.intValue() here is always valid since the above check guarantees that
         * the BigInteger is not too big to fit in an int (non-negative integer <= Integer.MAX_VALUE).
         * In other words, BigInteger.intValueExact() would never throw, meaning, it is not necessary.
         */
        return secureNumber.intValue();
    }

    @NotNull
    private static List<@NotNull String> convertToList(@Nullable final String toList) {
        return toList == null ? emptyList() : asList(toList.split("\\."));
    }

    public static class Version {
        private final int major;
        private final int minor;
        private final int patch;
        @NotNull
        private final List<@NotNull String> preRelease;
        @NotNull
        private final List<@NotNull String> build;

        Version(final int major, final int minor, final int patch, @NotNull final List<@NotNull String> preRelease, @NotNull final List<@NotNull String> build) {
            this.major = major;
            this.minor = minor;
            this.patch = patch;
            this.preRelease = preRelease;
            this.build = build;
        }

        Version(final int major, final int minor, final int patch) {
            this(major, minor, patch, emptyList(), emptyList());
        }

        public int getMajor() {
            return major;
        }

        public int getMinor() {
            return minor;
        }

        public int getPatch() {
            return patch;
        }

        @NotNull
        public List<@NotNull String> getPreRelease() {
            return preRelease;
        }

        @NotNull
        public List<@NotNull String> getBuild() {
            return build;
        }

        @Override
        public boolean equals(@Nullable final Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Version that = (Version) o;
            return major == that.major &&
                Objects.equals(minor, that.minor) &&
                Objects.equals(patch, that.patch) &&
                Objects.equals(preRelease, that.preRelease) &&
                Objects.equals(build, that.build);
        }

        @Override
        public int hashCode() {
            return Objects.hash(major, minor, patch, preRelease, build);
        }

        @Override
        @NotNull
        public String toString() {
            return new StringJoiner(", ", Version.class.getSimpleName() + "[", "]")
                .add("major=" + major)
                .add("minor=" + minor)
                .add("patch=" + patch)
                .add("preRelease=" + preRelease)
                .add("build=" + build)
                .toString();
        }
    }
}
