package org.semver4j.internal.range.processor;

class RangesUtils {
    private static final int X_RANGE_MARKER = -1;

    private RangesUtils() {
    }

    static int parseIntWithXSupport(String id) {
        if (id == null || id.equalsIgnoreCase("x") || id.equals("*") || id.equals("+")) {
            return X_RANGE_MARKER;
        }
        return Integer.parseInt(id);
    }

    static boolean isX(Integer id) {
        return id == X_RANGE_MARKER;
    }

    static boolean isNotBlank(String id) {
        return id != null && !id.isEmpty();
    }
}
