package org.semver4j;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class NpmSemverTest {
    public static Stream<Arguments> getParameters() {
        return Stream.of(
                // Fully-qualified versions:
                arguments("1.0.0", "1.0.0", true),
                arguments("1.0.0", "=1.0.0", true),
                arguments("1.2.3", "1.2.3", true),
                arguments("1.2.4", "1.2.3", false),
                arguments("1.0.0-setup-20220428123901", "1.0.0-setup-20220428123901", true),

                // Minor versions:
                arguments("1.2.3", "1.2", true),
                arguments("1.2.4", "1.3", false),

                // Major versions:
                arguments("1.2.3", "1", true),
                arguments("1.2.4", "2", false),

                // Hyphen ranges:
                arguments("1.2.4-beta+exp.sha.5114f85", "1.2.3 - 2.3.4", false),
                arguments("1.2.4", "1.2.3 - 2.3.4", true),
                arguments("1.2.3", "1.2.3 - 2.3.4", true),
                arguments("2.3.4", "1.2.3 - 2.3.4", true),
                arguments("2.3.0-alpha", "1.2.3 - 2.3.0-beta", true),
                arguments("2.3.4", "1.2.3 - 2.3", true),
                arguments("2.3.4", "1.2.3 - 2", true),
                arguments("4.4.0", "3.X - 4.X", true),
                arguments("1.0.0", "1.2.3 - 2.3.4", false),
                arguments("3.0.0", "1.2.3 - 2.3.4", false),
                arguments("2.4.3", "1.2.3 - 2.3", false),
                arguments("2.3.0-rc1", "1.2.3 - 2.3.0-beta", false),
                arguments("3.0.0", "1.2.3 - 2", false),

                // Wildcard ranges:
                arguments("3.1.5", "", true),
                arguments("3.1.5", "*", true),
                arguments("0.0.0", "*", true),
                arguments("1.0.0-beta", "*", false),
                arguments("3.1.5-beta", "3.1.x", false),
                arguments("3.1.5-beta+exp.sha.5114f85", "3.1.x", false),
                arguments("3.1.5+exp.sha.5114f85", "3.1.x", true),
                arguments("3.1.5", "3.1.x", true),
                arguments("3.1.5", "3.1.X", true),
                arguments("3.1.5", "3.x", true),
                arguments("3.1.5", "3.*", true),
                arguments("3.1.5", "3.1", true),
                arguments("3.1.5", "3", true),
                arguments("3.2.5", "3.1.x", false),
                arguments("3.0.5", "3.1.x", false),
                arguments("4.0.0", "3.x", false),
                arguments("2.0.0", "3.x", false),
                arguments("3.2.5", "3.1", false),
                arguments("3.0.5", "3.1", false),
                arguments("4.0.0", "3", false),
                arguments("2.0.0", "3", false),

                // Tilde ranges:
                arguments("1.2.4-beta", "~1.2.3", false),
                arguments("1.2.4-beta+exp.sha.5114f85", "~1.2.3", false),
                arguments("1.2.3", "~1.2.3", true),
                arguments("1.2.7", "~1.2.3", true),
                arguments("1.2.2", "~1.2", true),
                arguments("1.2.0", "~1.2", true),
                arguments("1.3.0", "~1", true),
                arguments("1.0.0", "~1", true),
                arguments("1.2.3", "~1.2.3-beta.2", true),
                arguments("1.2.3-beta.4", "~1.2.3-beta.2", true),
                arguments("1.2.4", "~1.2.3-beta.2", true),
                arguments("1.3.0", "~1.2.3", false),
                arguments("1.2.2", "~1.2.3", false),
                arguments("1.1.0", "~1.2", false),
                arguments("1.3.0", "~1.2", false),
                arguments("2.0.0", "~1", false),
                arguments("0.0.0", "~1", false),
                arguments("1.2.3-beta.1", "~1.2.3-beta.2", false),

                // Caret ranges:
                arguments("16.14.0", "^16.0.0-0", true),
                arguments("1.2.3", "^1.2.3", true),
                arguments("1.2.4", "^1.2.3", true),
                arguments("1.3.0", "^1.2.3", true),
                arguments("0.2.3", "^0.2.3", true),
                arguments("0.2.4", "^0.2.3", true),
                arguments("0.0.3", "^0.0.3", true),
                arguments("0.0.3+exp.sha.5114f85", "^0.0.3", true),
                arguments("0.0.3", "^0.0.3-beta", true),
                arguments("0.0.3-pr.2", "^0.0.3-beta", true),
                arguments("1.2.2", "^1.2.3", false),
                arguments("2.0.0", "^1.2.3", false),
                arguments("0.2.2", "^0.2.3", false),
                arguments("0.3.0", "^0.2.3", false),
                arguments("0.0.4", "^0.0.3", false),
                arguments("0.0.3-alpha", "^0.0.3-beta", false),
                arguments("0.0.4", "^0.0.3-beta", false),

                // Comparators:
                arguments("2.0.0", "=2.0.0", true),
                arguments("2.0.0", "=2.0", true),
                arguments("2.0.1", "=2.0", true),
                arguments("2.0.0", "=2", true),
                arguments("2.0.1", "=2", true),
                arguments("2.0.1", "=2.0.0", false),
                arguments("1.9.9", "=2.0.0", false),
                arguments("1.9.9", "=2.0", false),
                arguments("1.9.9", "=2", false),

                arguments("2.0.1", ">2.0.0", true),
                arguments("3.0.0", ">2.0.0", true),
                arguments("3.0.0", ">2.0", true),
                arguments("3.0.0", ">2", true),
                arguments("2.0.0", ">2.0.0", false),
                arguments("1.9.9", ">2.0.0", false),
                arguments("2.0.0", ">2.0", false),
                arguments("1.9.9", ">2.0", false),
                arguments("2.0.1", ">2", false),
                arguments("2.0.0", ">2", false),
                arguments("1.9.9", ">2", false),

                arguments("1.9.9", "<2.0.0", true),
                arguments("1.9.9", "<2.0", true),
                arguments("1.9.9", "<2", true),
                arguments("2.0.0", "<2.0.0", false),
                arguments("2.0.1", "<2.0.0", false),
                arguments("3.0.0", "<2.0.0", false),
                arguments("2.0.0", "<2.0", false),
                arguments("2.0.1", "<2.0", false),
                arguments("3.0.0", "<2.0", false),
                arguments("2.0.0", "<2", false),
                arguments("2.0.1", "<2", false),
                arguments("3.0.0", "<2", false),

                arguments("2.0.0", ">=2.0.0", true),
                arguments("2.0.1", ">=2.0.0", true),
                arguments("3.0.0", ">=2.0.0", true),
                arguments("2.0.0", ">=2.0", true),
                arguments("3.0.0", ">=2.0", true),
                arguments("2.0.0", ">=2", true),
                arguments("2.0.1", ">=2", true),
                arguments("3.0.0", ">=2", true),
                arguments("1.9.9", ">=2.0.0", false),
                arguments("1.9.9", ">=2.0", false),
                arguments("1.9.9", ">=2", false),

                arguments("1.9.9", "<=2.0.0", true),
                arguments("2.0.0", "<=2.0.0", true),
                arguments("1.9.9", "<=2.0", true),
                arguments("2.0.0", "<=2.0", true),
                arguments("2.0.1", "<=2.0", true),
                arguments("1.9.9", "<=2", true),
                arguments("2.0.0", "<=2", true),
                arguments("2.0.1", "<=2", true),
                arguments("2.0.1", "<=2.0.0", false),
                arguments("3.0.0", "<=2.0.0", false),
                arguments("3.0.0", "<=2.0", false),
                arguments("3.0.0", "<=2", false),

                // AND ranges:
                arguments("2.0.1", ">2.0.0 <3.0.0", true),
                arguments("2.0.1", ">2.0 <3.0", false),

                arguments("1.2.0", "1.2 <1.2.8", true),
                arguments("1.2.7", "1.2 <1.2.8", true),
                arguments("1.1.9", "1.2 <1.2.8", false),
                arguments("1.2.9", "1.2 <1.2.8", false),

                // OR ranges:
                arguments("1.2.3", "1.2.3 || 1.2.4", true),
                arguments("1.2.4", "1.2.3 || 1.2.4", true),
                arguments("1.2.5", "1.2.3 || 1.2.4", false),

                // Complex ranges:
                arguments("1.2.2", ">1.2.1 <1.2.8 || >2.0.0", true),
                arguments("1.2.7", ">1.2.1 <1.2.8 || >2.0.0", true),
                arguments("2.0.1", ">1.2.1 <1.2.8 || >2.0.0", true),
                arguments("1.2.1", ">1.2.1 <1.2.8 || >2.0.0", false),
                arguments("2.0.0", ">1.2.1 <1.2.8 || >2.0.0", false),

                arguments("1.2.2", ">1.2.1 <1.2.8 || >2.0.0 <3.0.0", true),
                arguments("1.2.7", ">1.2.1 <1.2.8 || >2.0.0 <3.0.0", true),
                arguments("2.0.1", ">1.2.1 <1.2.8 || >2.0.0 <3.0.0", true),
                arguments("2.5.0", ">1.2.1 <1.2.8 || >2.0.0 <3.0.0", true),
                arguments("1.2.1", ">1.2.1 <1.2.8 || >2.0.0 <3.0.0", false),
                arguments("1.2.8", ">1.2.1 <1.2.8 || >2.0.0 <3.0.0", false),
                arguments("2.0.0", ">1.2.1 <1.2.8 || >2.0.0 <3.0.0", false),
                arguments("3.0.0", ">1.2.1 <1.2.8 || >2.0.0 <3.0.0", false),

                arguments("1.2.2", "1.2.2 - 1.2.7 || 2.0.1 - 2.9.9", true),
                arguments("1.2.7", "1.2.2 - 1.2.7 || 2.0.1 - 2.9.9", true),
                arguments("2.0.1", "1.2.2 - 1.2.7 || 2.0.1 - 2.9.9", true),
                arguments("2.5.0", "1.2.2 - 1.2.7 || 2.0.1 - 2.9.9", true),
                arguments("1.2.1", "1.2.2 - 1.2.7 || 2.0.1 - 2.9.9", false),
                arguments("1.2.8", "1.2.2 - 1.2.7 || 2.0.1 - 2.9.9", false),
                arguments("2.0.0", "1.2.2 - 1.2.7 || 2.0.1 - 2.9.9", false),
                arguments("3.0.0", "1.2.2 - 1.2.7 || 2.0.1 - 2.9.9", false),

                arguments("1.2.0", "1.2 <1.2.8 || >2.0.0", true),
                arguments("1.2.7", "1.2 <1.2.8 || >2.0.0", true),
                arguments("1.2.7", "1.2 <1.2.8 || >2.0.0", true),
                arguments("2.0.1", "1.2 <1.2.8 || >2.0.0", true),
                arguments("1.1.0", "1.2 <1.2.8 || >2.0.0", false),
                arguments("1.2.9", "1.2 <1.2.8 || >2.0.0", false),
                arguments("2.0.0", "1.2 <1.2.8 || >2.0.0", false),

                arguments("1.2.2", " ~> 1.2.3 ", false),
                arguments("1.2.3", " ~> 1.2.3 ", true),
                arguments("1.2.4", " ~> 1.2.3 ", true),
                arguments("1.3.0", " ~> 1.2.3 ", false),
                arguments("2.2.0", " ~> 2.2 ", true),
                arguments("2.3.0", " ~> 2.2 ", false),

                arguments("0.0.9", "[1.0,2.0]", false),
                arguments("1.0.0", "[1.0,2.0]", true),
                arguments("2.0.0", "[1.0,2.0]", true),
                arguments("1.5.6", "[1.0,2.0]", true),
                arguments("2.0.1", "[1.0,2.0]", false),

                arguments("2.0.0", "[1.0,2.0[", false),
                arguments("1.0.0", "[1.0,2.0[", true),
                arguments("0.0.9", "[1.0,2.0[", false),
                arguments("2.0.1", "[1.0,2.0[", false),
                arguments("1.5.6", "[1.0,2.0[", true),

                arguments("1.0.0", "]1.0,2.0]", false),
                arguments("1.5.6", "]1.0,2.0]", true),
                arguments("2.0.0", "]1.0,2.0]", true),
                arguments("2.0.1", "]1.0,2.0]", false),

                arguments("1.0.0", "]1.0,2.0[", false),
                arguments("2.0.0", "]1.0,2.0[", false),
                arguments("1.5.6", "]1.0,2.0[", true),

                arguments("1.0.0", "[1.0,)", true),
                arguments("1.0.100", "[1.0,)", true),
                arguments("100.0.1", "[1.0,)", true),
                arguments("0.0.9", "[1.0,)", false),

                arguments("1.0.0", "]1.0,)", false),
                arguments("1.0.100", "]1.0,)", true),
                arguments("100.0.1", "]1.0,)", true),
                arguments("0.0.9", "]1.0,)", false),

                arguments("2.0.0", "(,2.0]", true),
                arguments("2.0.10", "(,2.0]", false),
                arguments("3.0.10", "(,2.0]", false),
                arguments("1.0.100", "(,2.0]", true),
                arguments("0.0.9", "(,2.0]", true),

                arguments("2.0.0", "(,2.0[", false),
                arguments("2.0.10", "(,2.0[", false),
                arguments("3.0.10", "(,2.0[", false),
                arguments("1.0.100", "(,2.0[", true),
                arguments("0.0.9", "(,2.0[", true),

                arguments("1.2.0", "1.2.+", true),
                arguments("1.1.90", "1.2.+", false),
                arguments("1.3.0", "1.2.+", false),
                arguments("1.2.90", "1.2.+", true),

                arguments("1.0.0", "1.+", true),
                arguments("2.0.0", "1.+", false),
                arguments("2.0.1", "1.+", false),
                arguments("1.3.0", "1.+", true),
                arguments("1.2.90", "1.+", true),

                arguments("0.0.0", "latest.integration", true)
        );
    }

    @ParameterizedTest
    @MethodSource("getParameters")
    public void test(String version, String rangeExpression, boolean expected) {
        boolean satisfies = new Semver(version).satisfies(rangeExpression);
        assertEquals(expected, satisfies, version + " , " + rangeExpression);
    }
}
