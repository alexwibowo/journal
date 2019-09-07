package com.isolution.journal;

public final class MessageAndTime {
    public final String message;
    public final long originalTime;
    public final long timeNanos;

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
