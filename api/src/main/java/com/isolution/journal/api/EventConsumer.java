package com.isolution.journal.api;

public interface EventConsumer<$Event> {

    void onMessage(long eventTimeNanos,
                   long messageIndex,
                   $Event event);
}
