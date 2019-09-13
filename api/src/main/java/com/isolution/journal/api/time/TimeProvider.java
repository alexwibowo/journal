package com.isolution.journal.api.time;

@FunctionalInterface
public interface TimeProvider {

    long currentTimeMillis();

    default long currentTimeMicros() {
        return currentTimeMillis() * 1000;
    }

    default long currentTimeNanos() {
        return currentTimeMicros() * 1000;
    }
}
