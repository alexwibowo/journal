package com.isolution.journal.chronicle;

import com.isolution.journal.api.EventAppender;
import com.isolution.journal.api.EventConsumer;
import com.isolution.journal.api.EventQueue;
import com.isolution.journal.api.EventReader;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.wire.DocumentContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

public final class ChronicleJournal implements EventQueue<ChronicleEvent> {

    private final ChronicleQueue chronicleQueue;
    private final Consumer<ChronicleEvent> eventConsumer;

    public ChronicleJournal(final @NotNull ChronicleQueue chronicleQueue,
                            final @NotNull Consumer<ChronicleEvent> eventConsumer) {
        this.chronicleQueue = requireNonNull(chronicleQueue);
        this.eventConsumer = requireNonNull(eventConsumer);
    }


    @Override
    public ChronicleEventReader reader() {
        return new ChronicleEventReader(chronicleQueue.createTailer());
    }

    @Override
    public @NotNull ChronicleJournalWriter appender() {
        return new ChronicleJournalWriter(chronicleQueue.acquireAppender());
    }

    public static class ChronicleJournalWriter implements EventAppender<ChronicleEvent> {
        private final ExcerptAppender appender;
        private final ChronicleEventFrame eventFrame;

        public ChronicleJournalWriter(final ExcerptAppender appender) {
            this.appender = appender;
            this.eventFrame = new ChronicleEventFrame();
        }

        @Override
        public void appendEvent(final ChronicleEvent event) {
            try (final DocumentContext documentContext = appender.writingDocument()){
                final BytesOut<?> bytes = documentContext.wire().bytes();
                eventFrame.prepare(bytes);

                long eventTime  = 1; // FIXME: source event time properly
                final ChronicleEventFrame.Writer writer = eventFrame.newEvent(eventTime);
                writer.write(event::writeTo);
            }
        }
    }


    public static class ChronicleEventReader implements EventReader<ChronicleEvent> {
        private final ExcerptTailer tailer;

        private final ChronicleEventFrame eventFrame;


        public ChronicleEventReader(final @NotNull ExcerptTailer tailer) {
            this.tailer = tailer;
            this.eventFrame = new ChronicleEventFrame();
        }

        @Override
        public boolean read(final @NotNull EventConsumer<ChronicleEvent> eventConsumer) {
            final DocumentContext documentContext = tailer.readingDocument();
            if (documentContext.isPresent()) {
                final BytesIn bytes = documentContext.wire().bytes();
                eventFrame.reader(marshaller).read(bytes, eventConsumer);

            }
            return false;
        }
    }
}
