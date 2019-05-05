package com.isolution.journal.api;

import org.jetbrains.annotations.NotNull;

public final class DefaultEngine<$InputEvent, $OutputEvent> implements Engine<$InputEvent, $OutputEvent> {

    private final EventReader<$InputEvent> inputEventReader;
    private final EventReader<$OutputEvent> outputEventReader;
    private final EventAppender<$OutputEvent> outputEventAppender;
    private final EventProcessor<$InputEvent, $OutputEvent> eventProcessor;

    public DefaultEngine(final @NotNull EventQueue<$InputEvent> inputEventQueue,
                         final @NotNull EventQueue<$OutputEvent> outputEventQueue,
                         final @NotNull EventProcessor<$InputEvent, $OutputEvent> eventProcessor) {
        this.inputEventReader = inputEventQueue.reader();
        this.outputEventReader = outputEventQueue.reader();
        this.outputEventAppender = outputEventQueue.appender();
        this.eventProcessor = eventProcessor;
    }

    @Override
    public EngineProcessingResult processOne() {
        // replay all events in output queue, to bring engine's state up to date
        final boolean readOutputEvent = outputEventReader.read(new EventConsumer<$OutputEvent>() {
            @Override
            public void onEvent(final long eventTimeNanos,
                                final long messageIndex,
                                final $OutputEvent event) {
                onOutputEvent(eventTimeNanos, messageIndex, event);
            }
        });


        if (readOutputEvent) {
            return EngineProcessingResult.PROCESSED_OUTPUT;
        }

        // when there is no more events in output queue, start reading from input queue
        final boolean readInputEvent = inputEventReader.read(new EventConsumer<$InputEvent>() {
            @Override
            public void onEvent(final long eventTimeNanos,
                                final long messageIndex,
                                final $InputEvent event) {
                onInputEvent(eventTimeNanos, messageIndex, event);
            }
        });

        if (readInputEvent) {
            return EngineProcessingResult.PROCESSED_INPUT;
        }

        return EngineProcessingResult.IDLE;
    }

    private void onOutputEvent(final long eventTimeNanos,
                               final long messageIndex,
                               final $OutputEvent outputEvent) {
        eventProcessor.applyOutputEvent(eventTimeNanos, messageIndex, outputEvent);
    }

    private void onInputEvent(final long eventTimeNanos,
                              final long messageIndex,
                              final $InputEvent inputEvent) {
        final $OutputEvent output = eventProcessor.process(eventTimeNanos, messageIndex, inputEvent);
        outputEventAppender.appendEvent(output);
    }
}
