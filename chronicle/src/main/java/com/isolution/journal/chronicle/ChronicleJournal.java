package com.isolution.journal.chronicle;

import com.isolution.journal.api.*;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.wire.DocumentContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

public final class ChronicleJournal<$Event> implements EventQueue<$Event> {

    private final ChronicleQueue chronicleQueue;
    private final Consumer<$Event> eventConsumer;

    public ChronicleJournal(final @NotNull ChronicleQueue chronicleQueue,
                            final @NotNull Consumer<$Event> eventConsumer) {
        this.chronicleQueue = requireNonNull(chronicleQueue);
        this.eventConsumer = requireNonNull(eventConsumer);
    }


    @Override
    public EventReader<$Event> reader() {
        return new ChronicleEventReader<>(chronicleQueue.createTailer());
    }

    @Override
    public @NotNull EventAppender<$Event> appender() {
        return null;
    }

    public static class ChronicleEventReader<$Event> implements EventReader<$Event> {
        private final ExcerptTailer tailer;

        private final ChronicleEventFrame eventFrame;


        public ChronicleEventReader(final @NotNull ExcerptTailer tailer) {
            this.tailer = tailer;
            this.eventFrame = new ChronicleEventFrame();
        }

        @Override
        public boolean read(final @NotNull EventConsumer<$Event> eventConsumer) {
            final DocumentContext documentContext = tailer.readingDocument();
            if (documentContext.isPresent()) {
                final BytesIn bytes = documentContext.wire().bytes();
                eventFrame.reader(marshaller).read(bytes, eventConsumer);

            }
            return false;
        }
    }
}
