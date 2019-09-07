package com.isolution.journal.inmemory;

import com.isolution.journal.api.EventAppender;
import com.isolution.journal.api.EventConsumer;
import com.isolution.journal.api.EventQueue;
import com.isolution.journal.api.EventReader;
import net.openhft.chronicle.core.time.TimeProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.LinkedBlockingQueue;

public final class InMemoryJournal<$Event> implements EventQueue<$Event> {

    private LinkedBlockingQueue<$Event> queue = new LinkedBlockingQueue<$Event>();

    @NotNull
    private final TimeProvider timeProvider;

    public InMemoryJournal(final @NotNull TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    @Override
    @NotNull
    public EventReader<$Event> reader() {
        return new Reader();
    }

    @Override
    @NotNull
    public EventAppender<$Event> appender() {
        return new Appender();
    }

    private class Reader implements EventReader<$Event> {
        private long messageIndex;

        @Override
        public boolean read(final EventConsumer<$Event> eventConsumer) {
            final $Event event = queue.poll();
            if (event != null) {
                eventConsumer.onEvent(timeProvider.currentTimeNanos(), messageIndex++, event);
                return true;
            } else {
                return false;
            }
        }
    }

    private class Appender implements EventAppender<$Event> {

        @Override
        public void appendEvent(final $Event event) {
            queue.offer(event);
        }
    }
}
