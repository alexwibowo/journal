package com.isolution.journal.api;

import org.jetbrains.annotations.NotNull;

public interface EventQueue<$Event> {

    /**
     * @return reader for the event queue. Note that reader is not thread-safe, and should not be used across different threads.
     */
    EventReader<$Event> reader();

    @NotNull
    EventAppender<$Event> appender();
}
