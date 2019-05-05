package com.isolution.journal.api;

public interface Engine<$InputEvent, $OutputEvent> {

    EngineProcessingResult processOne();
}
