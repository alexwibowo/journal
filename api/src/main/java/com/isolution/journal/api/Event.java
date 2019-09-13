package com.isolution.journal.api;

public interface Event<$InputSource, $OutputDestination> {

    void readFrom($InputSource source);

    void writeTo($OutputDestination destination);
}
