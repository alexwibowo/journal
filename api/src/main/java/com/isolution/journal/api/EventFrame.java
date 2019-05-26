package com.isolution.journal.api;

public final class EventFrame {

    private long length;
    private long createTimeNanos;

    public void reset() {
        length = 0;
        createTimeNanos = -1;
    }

    public void newEvent(final long createTimeNanos) {
        this.createTimeNanos = createTimeNanos;


    }
}
