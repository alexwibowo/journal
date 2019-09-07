package com.isolution.journal.api;

public interface Event<$Input, $Output> {

    void readFrom($Input source);

    void writeTo($Output destination);
}
