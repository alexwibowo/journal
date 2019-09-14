package com.isolution.journal.chronicle;

import com.isolution.journal.api.Event;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesMarshallable;
import net.openhft.chronicle.bytes.BytesOut;
import org.jetbrains.annotations.NotNull;

public interface ChronicleEvent extends Event<BytesIn, BytesOut>, BytesMarshallable {

    byte eventType();

    @Override
    default void readFrom(final @NotNull BytesIn source){
        readMarshallable(source);
    }

    @Override
    default void writeTo(final @NotNull BytesOut destination){
        writeMarshallable(destination);
    }
}
