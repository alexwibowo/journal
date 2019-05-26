package com.isolution.journal.chronicle;

import com.isolution.journal.api.EventConsumer;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ChronicleEventFrame {


    private long createTimeNanos;
    private BytesOut bytesOut;
    private long startOfFrame;
    private FieldLength fieldLength = new FieldLength();
    private Writer writer = new Writer();

    public long getCreateTimeNanos() {
        return createTimeNanos;
    }

    public void reset() {
        createTimeNanos = -1;
    }

    public void prepare(final @NotNull BytesOut bytesOut) {
        this.bytesOut = bytesOut;
    }

    public Writer newEvent(final long createTimeNanos) {
        this.createTimeNanos = createTimeNanos;
        // remember start of this frame, in case we need to go back to
        this.startOfFrame = bytesOut.writePosition();
        return writer;
    }

    public Reader reader(Marshaller marshaller) {
        return new Reader(marshaller);
    }

    class Writer {

        void write(final Consumer<BytesOut> bytesOutConsumer){
            bytesOut.writeByte(EventFrameCode.COMPLETE_EVENT.code);
            bytesOut.writeLong(createTimeNanos);
            try (final AutoCloseable ignored = fieldLength.writeLength(bytesOut)) {
                bytesOutConsumer.accept(bytesOut);
            } catch (final Throwable e) {
                // upon error, go back to the start and indicate that this is an incomplete document
                bytesOut.writePosition(startOfFrame);
                bytesOut.writeByte(EventFrameCode.INCOMPLETE_EVENT.code);
            }
        }
    }

    class Reader<$Event> {
        private final Marshaller<$Event> marshaller;

        Reader(Marshaller<$Event> marshaller) {
            this.marshaller = marshaller;
        }

        public void read(final BytesIn bytesIn,
                         final EventConsumer<$Event> eventConsumer) {
            final EventFrameCode eventFrameCode = EventFrameCode.from(bytesIn.readByte());
            final long createTimeNanos = bytesIn.readLong();
            try (final AutoCloseable ignored = fieldLength.readLength(bytesIn)) {
                $Event unmarshall = marshaller.unmarshall(bytesIn);
                eventConsumer.onEvent(
                        createTimeNanos,
                        0,
                        unmarshall);
            } catch (final Throwable throwable) {

            }
        }
    }

    static class Marshaller<E> {

        E unmarshall(final BytesIn bytesIn) {
            return null;
        }

    }

    static class FieldLength {
        LengthUpdater lengthUpdater = new LengthUpdater();
        LengthReader lengthReader = new LengthReader();

        public AutoCloseable writeLength(final @NotNull BytesOut bytesOut) {
            return lengthUpdater.begin(bytesOut);
        }
        public AutoCloseable readLength(final @NotNull BytesIn bytesIn) {
            return lengthReader.begin(bytesIn);
        }

        public static class LengthUpdater implements AutoCloseable {

            private long startPosition;
            private BytesOut bytesOut;

            public LengthUpdater begin(final @NotNull BytesOut bytesOut) {
                this.bytesOut = bytesOut;
                startPosition = bytesOut.writePosition();
                bytesOut.writeLong(0L);
                return this;
            }

            @Override
            public void close()  {
                final long end = bytesOut.writePosition();
                bytesOut.writePosition(startPosition);
                bytesOut.writeLong(end - startPosition);
                bytesOut.writePosition(end);
            }
        }

        public static class LengthReader implements AutoCloseable{
            private BytesIn bytesIn;
            private long startPosition;
            private long length;

            public LengthReader begin(final @NotNull BytesIn bytesIn) {
                this.bytesIn = bytesIn;
                startPosition = bytesIn.readPosition();
                length = bytesIn.readLong();
                return this;
            }

            @Override
            public void close() {
                bytesIn.readPosition(startPosition + length);
            }
        }
    }

    enum EventFrameCode {
        // indicate that the frame is a complete one, written without error
        COMPLETE_EVENT((byte) 0),
        INCOMPLETE_EVENT((byte) 1);

        public static EventFrameCode[] VALUES = new EventFrameCode[values().length];
        static {
            for (EventFrameCode value : values()) {
                VALUES[value.code] = value;
            }
        }

        final byte code;


        EventFrameCode(byte code) {
            this.code = code;
        }

        static EventFrameCode from(byte code) {
            return VALUES[code];
        }
    }
}
