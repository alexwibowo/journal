package com.isolution.journal.api;

public enum EngineProcessingResult {
    /**
     * Engine has just processed an event from the input queue
     */
    PROCESSED_INPUT,
    /**
     * Engine has just processed an event from the output queue
     */
    PROCESSED_OUTPUT,
    /**
     * Engine has not processed any event from input nor output queue
     */
    IDLE
}