package com.isolution.journal.api;

public interface EventConsumer<$Event> {

    void onEvent(long eventTimeNanos,
                 long messageIndex,
                 $Event event);
}
