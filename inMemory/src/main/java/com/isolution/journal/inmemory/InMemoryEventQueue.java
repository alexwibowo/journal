package com.isolution.journal.inmemory;

import com.isolution.journal.api.EventAppender;
import com.isolution.journal.api.EventConsumer;
import com.isolution.journal.api.EventQueue;
import com.isolution.journal.api.EventReader;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public final class InMemoryEventQueue<$Event> implements EventQueue<$Event> {

    private LinkedBlockingQueue<$Event> queue = new LinkedBlockingQueue<$Event>();

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
            try {
                final $Event event = queue.poll(1, TimeUnit.MILLISECONDS);
                if (event != null) {
                    // TODO: change System.nanoTime() with TimeProvider
                    eventConsumer.onMessage(System.nanoTime(), messageIndex++, event);
                    return true;
                } else {
                    return false;
                }
            } catch (final InterruptedException e) {
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
