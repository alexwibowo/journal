package com.isolution.journal.api;

public interface EventAppender<$Event> {

    void appendEvent($Event event);
}
