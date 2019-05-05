package com.isolution.journal.inmemory;

import com.isolution.journal.api.EventAppender;
import com.isolution.journal.api.EventReader;
import net.openhft.chronicle.core.time.SetTimeProvider;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class InMemoryEventQueueTest {


    private InMemoryEventQueue<String> inMemoryEventQueue;
    private List<JournalEvent> consumedEvents = new ArrayList<>();
    private SetTimeProvider timeProvider;
    private @NotNull EventAppender<String> appender;
    private @NotNull EventReader<String> reader;

    @BeforeEach
    void setUp() {
        timeProvider = new SetTimeProvider(100);
        timeProvider.autoIncrement(1L, TimeUnit.NANOSECONDS);
        inMemoryEventQueue = new InMemoryEventQueue<>(timeProvider);
        appender = inMemoryEventQueue.appender();
        reader = inMemoryEventQueue.reader();
    }

    @Test
    void all_written_events_can_be_read() {
        appender.appendEvent("Hello " + 1);
        appender.appendEvent("Hello " + 2);

        reader.read((eventTimeNanos, messageIndex, message) -> consumedEvents.add(new JournalEvent(eventTimeNanos, messageIndex, message)));
        reader.read((eventTimeNanos, messageIndex, message) -> consumedEvents.add(new JournalEvent(eventTimeNanos, messageIndex, message)));

        assertThat(consumedEvents).hasSize(2);
        assertThat(consumedEvents)
                .extracting(JournalEvent::getEventTimeNanos, JournalEvent::getMessageIndex, JournalEvent::getMessage)
                .containsExactly(
                        tuple(100L, 0L, "Hello 1"),
                        tuple(101L, 1L, "Hello 2")
                );
    }

    static class JournalEvent {
        final long eventTimeNanos;
        final long messageIndex;
        final String message;

        JournalEvent(long eventTimeNanos, long messageIndex, String message) {
            this.eventTimeNanos = eventTimeNanos;
            this.messageIndex = messageIndex;
            this.message = message;
        }

        long getEventTimeNanos() {
            return eventTimeNanos;
        }

        long getMessageIndex() {
            return messageIndex;
        }

        String getMessage() {
            return message;
        }
    }
}

