package com.isolution.journal.api;

public interface EventReader<$Event extends Event> {
    boolean read(EventConsumer<$Event> eventConsumer);
}
