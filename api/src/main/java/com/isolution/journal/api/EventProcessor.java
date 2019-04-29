package com.isolution.journal.api;

public interface EventProcessor<$InputEvent, $OutputEvent> {

    <E extends $InputEvent> $OutputEvent process(long eventTimeNanos,
                                                 long messageIndex,
                                                 E event);

    void applyOutputEvent(long eventTimeNanos,
                          long messageIndex,
                          $OutputEvent outputEvent);
}
