package com.isolution.journal.inmemory;

import com.isolution.journal.MessageAndTime;
import com.isolution.journal.api.*;
import net.openhft.chronicle.core.time.SystemTimeProvider;

public class InMemoryEventQueueDemo {

    public static void main(String[] args) {
        final SystemTimeProvider timeProvider = SystemTimeProvider.INSTANCE;
        final EventQueue<MessageAndTime> inputQueue = new InMemoryJournal<>(timeProvider);
        final EventQueue<MessageAndTime> outputQueue = new InMemoryJournal<>(timeProvider);
        final DefaultEngine defaultEngine = new DefaultEngine<>(inputQueue, outputQueue, new EventProcessor<MessageAndTime, MessageAndTime>() {
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

        final EventAppender<MessageAndTime> inputQueueAppender = inputQueue.appender();
        inputQueueAppender.appendEvent(MessageAndTime.requestMessage("Hello", timeProvider.currentTimeNanos()));
        inputQueueAppender.appendEvent(MessageAndTime.requestMessage("Hello", timeProvider.currentTimeNanos()));

        EngineProcessingResult result;
        while ( (result = defaultEngine.processOne()) != EngineProcessingResult.IDLE) {
        }
    }

}
