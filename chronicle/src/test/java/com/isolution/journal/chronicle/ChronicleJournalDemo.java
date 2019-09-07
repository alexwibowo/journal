package com.isolution.journal.chronicle;

import com.isolution.journal.MessageAndTime;
import com.isolution.journal.api.DefaultEngine;
import com.isolution.journal.api.EngineProcessingResult;
import com.isolution.journal.api.EventAppender;
import com.isolution.journal.api.EventProcessor;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.function.Consumer;

class ChronicleJournalDemo {

    @TempDir
    private Path inputQueueFolder;

    @TempDir
    private Path outputQueueFolder;

    private ChronicleJournal<MessageAndTime> inputJournal;
    private ChronicleJournal<MessageAndTime> outputJournal;

    @BeforeEach
    void setUp() {
        final SingleChronicleQueue inputQueue = SingleChronicleQueueBuilder.binary(inputQueueFolder).build();
        final SingleChronicleQueue outputQueue = SingleChronicleQueueBuilder.binary(outputQueueFolder).build();
        inputJournal = new ChronicleJournal<>(inputQueue, messageAndTime -> {

        });
        outputJournal = new ChronicleJournal<>(outputQueue, messageAndTime -> {
        });
        final DefaultEngine defaultEngine = new DefaultEngine<>(inputJournal, outputJournal, new EventProcessor<MessageAndTime, MessageAndTime>() {
            @Override
            public MessageAndTime process(final long eventTimeNanos,
                                          final long messageIndex,
                                          final MessageAndTime event) {
                return MessageAndTime.responseMessage(event.message, event.timeNanos, eventTimeNanos);
            }

            @Override
            public void applyOutputEvent(final long eventTimeNanos,
                                         final long messageIndex,
                                         final MessageAndTime outputEvent) {
                System.out.println(String.format("%d %d % d", eventTimeNanos, outputEvent.originalTime, outputEvent.timeNanos));
                System.out.println(String.format("%d", eventTimeNanos - outputEvent.originalTime));
                System.out.println(String.format("%d", eventTimeNanos - outputEvent.timeNanos));
                System.out.println(String.format("%d", outputEvent.latency()));
            }
        });

        EventAppender<MessageAndTime> appender = inputJournal.appender();
        appender.appendEvent(MessageAndTime.requestMessage("Hello", timeProvider.currentTimeNanos()));
        appender.appendEvent(MessageAndTime.requestMessage("Hello", timeProvider.currentTimeNanos()));
        while ( (result = defaultEngine.processOne()) != EngineProcessingResult.IDLE) {
        }
    }
}