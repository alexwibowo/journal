package com.isolution.journal.api;

public interface EventReader<$Event> {
    boolean read(EventConsumer<$Event> eventConsumer);
}
