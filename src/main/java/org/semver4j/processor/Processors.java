package org.semver4j.processor;

import java.util.List;

public class Processors {
    private Processors() {}

    public static final List<Processor> ALL_PROCESSORS =
            List.of(allVersions(), ivy(), hyphen(), caret(), tilde(), xRange());

    public static AllVersionsProcessor allVersions() {
        return new AllVersionsProcessor();
    }

    public static IvyProcessor ivy() {
        return new IvyProcessor();
    }

    public static HyphenProcessor hyphen() {
        return new HyphenProcessor();
    }

    public static CaretProcessor caret() {
        return new CaretProcessor();
    }

    public static TildeProcessor tilde() {
        return new TildeProcessor();
    }

    public static XRangeProcessor xRange() {
        return new XRangeProcessor();
    }
}
