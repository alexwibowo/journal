package com.isolution.journal.chronicle;

import com.isolution.journal.MessageAndTime;
import com.isolution.journal.api.DefaultEngine;
import com.isolution.journal.api.EngineProcessingResult;
import com.isolution.journal.api.EventAppender;
import com.isolution.journal.api.EventProcessor;
import com.isolution.journal.api.time.ChronicleTimeProvider;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.wire.AbstractBytesMarshallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.function.Consumer;

class ChronicleJournalDemo {

    @TempDir
    private Path inputQueueFolder;

    @TempDir
    private Path outputQueueFolder;

    private ChronicleJournal inputJournal;
    private ChronicleJournal outputJournal;

    public static class Foo extends AbstractBytesMarshallable {

    }


    @BeforeEach
    void setUp() {
        final SingleChronicleQueue inputQueue = SingleChronicleQueueBuilder.binary(inputQueueFolder).build();
        final SingleChronicleQueue outputQueue = SingleChronicleQueueBuilder.binary(outputQueueFolder).build();
        final ChronicleTimeProvider chronicleTimeProvider = new ChronicleTimeProvider();

        final Consumer<ChronicleEvent> inputEventConsumer = messageAndTime -> {};
        inputJournal = new ChronicleJournal(inputQueue, inputEventConsumer, chronicleTimeProvider);

        final Consumer<ChronicleEvent> outputEventConsumer = messageAndTime -> {};
        outputJournal = new ChronicleJournal(outputQueue, outputEventConsumer, chronicleTimeProvider);

        EventProcessor<ChronicleEvent, ChronicleEvent> eventProcessor = new EventProcessor<ChronicleEvent, ChronicleEvent>() {
            @Override
            public ChronicleEvent process(final long eventTimeNanos,
                                          final long messageIndex,
                                          final ChronicleEvent event) {
                return MessageAndTime.responseMessage(event.message, event.timeNanos, eventTimeNanos);
            }

            @Override
            public void applyOutputEvent(final long eventTimeNanos,
                                         final long messageIndex,
                                         final ChronicleEvent outputEvent) {
                System.out.println(String.format("%d %d % d", eventTimeNanos, outputEvent.originalTime, outputEvent.timeNanos));
                System.out.println(String.format("%d", eventTimeNanos - outputEvent.originalTime));
                System.out.println(String.format("%d", eventTimeNanos - outputEvent.timeNanos));
                System.out.println(String.format("%d", outputEvent.latency()));
            }
        };
        final DefaultEngine defaultEngine = new DefaultEngine<ChronicleEvent, ChronicleEvent>(inputJournal, outputJournal, eventProcessor);

        final EventAppender<ChronicleEvent> appender = inputJournal.appender();
        appender.appendEvent(MessageAndTime.requestMessage("Hello", chronicleTimeProvider.currentTimeNanos()));
        appender.appendEvent(MessageAndTime.requestMessage("Hello", chronicleTimeProvider.currentTimeNanos()));
        while ( (result = defaultEngine.processOne()) != EngineProcessingResult.IDLE) {
        }
    }
}