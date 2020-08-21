package com.gitlab.jeeto.oboco.opds.opds;

import static java.lang.String.format;

public class Preconditions {

    private Preconditions() {
    }

    public static void checkState(boolean condition, String msg, Object... args) {
        if (!condition) {
            throw new IllegalStateException(format(msg, args));
        }
    }
}
