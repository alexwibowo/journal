package com.isolution.journal.api.time;

import net.openhft.chronicle.core.time.SystemTimeProvider;

public class ChronicleTimeProvider implements TimeProvider{

    @Override
    public long currentTimeMillis() {
        return SystemTimeProvider.INSTANCE.currentTimeMillis();
    }

    @Override
    public long currentTimeMicros() {
        return SystemTimeProvider.INSTANCE.currentTimeMicros();
    }

    @Override
    public long currentTimeNanos() {
        return SystemTimeProvider.INSTANCE.currentTimeNanos();
    }
}
