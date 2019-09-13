package com.isolution.journal.api;

public interface Engine<$InputEvent extends Event, $OutputEvent extends Event> {

    EngineProcessingResult processOne();
}
