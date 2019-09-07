package com.isolution.journal.chronicle;

import com.isolution.journal.api.Event;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

public interface ChronicleEvent extends Event<BytesIn, BytesOut> {

    @Override
    void readFrom(BytesIn source);

    @Override
    void writeTo(BytesOut destination);
}
