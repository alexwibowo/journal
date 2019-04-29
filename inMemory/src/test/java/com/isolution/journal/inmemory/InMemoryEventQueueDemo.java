package com.isolution.journal.inmemory;

import com.isolution.journal.api.DefaultEngine;
import com.isolution.journal.api.EventAppender;
import com.isolution.journal.api.EventProcessor;
import com.isolution.journal.api.EventQueue;

public class InMemoryEventQueueDemo {

    public static void main(String[] args) {
        final EventQueue<MessageAndTime> inputQueue = new InMemoryEventQueue<>();
        final EventQueue<MessageAndTime> outputQueue = new InMemoryEventQueue<>();
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
        inputQueueAppender.appendEvent(MessageAndTime.requestMessage("Hello", System.nanoTime()));
        inputQueueAppender.appendEvent(MessageAndTime.requestMessage("Hello", System.nanoTime()));

        while (defaultEngine.processOne()) {
        }
    }

    private static class MessageAndTime {
        private String message;
        private long originalTime;
        private long timeNanos;

        public static MessageAndTime requestMessage(final String message,
                                                    final long timeNanos) {
            return new MessageAndTime(message, Long.MIN_VALUE, timeNanos);
        }

        public static MessageAndTime responseMessage(final String message,
                                                     final long originalTime,
                                                     final long timeNanos) {
            return new MessageAndTime(message, originalTime, timeNanos);
        }

        public MessageAndTime(final String message,
                              final long originalTime,
                              final long timeNanos) {
            this.message = message;
            this.originalTime = originalTime;
            this.timeNanos = timeNanos;
        }

        public long latency() {
            return timeNanos - originalTime;
        }
    }
}
