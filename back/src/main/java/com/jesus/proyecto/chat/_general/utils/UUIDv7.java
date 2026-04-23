package com.jesus.proyecto.chat._general.utils;

import java.security.SecureRandom;
import java.time.Clock;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public final class UUIDv7 {

    private final AtomicLong state = new AtomicLong(0L);
    private final SecureRandom random = new SecureRandom();
    private final Clock clock;

    private final static UUIDv7 SHARED = new UUIDv7(Clock.systemUTC());

    UUIDv7(Clock clock) {
        this.clock = clock;
    }

    UUID generate() {
        while (true) {
            long currentState = state.get();
            long prevTimestamp = currentState >>> 12; 
            int prevSequence = (int) (currentState & 0xFFFL);
            long timestamp = this.clock.millis();
            long sequence;
            if (timestamp > prevTimestamp) {
                sequence = 0;
            }
            else {
                timestamp = prevTimestamp;
                sequence = prevSequence + 1;
                if (sequence > 0xFFF) {
                    Thread.onSpinWait();
                    while (clock.millis() <= prevTimestamp) {
                        Thread.onSpinWait();
                    }
                    continue;
                }
            }
            long nextState = (timestamp << 12) | sequence;
            if (state.compareAndSet(currentState, nextState)) {
                return buildUUID(timestamp, sequence);
            }
        }
    }

    private UUID buildUUID(long timestamp, long sequence) {
        long msb = (timestamp & 0xFFFFFFFFFFFFL) << 16;
        msb |= 0x7000L;
        long randomBits12 = random.nextInt(1 << 12);
        msb |= ((sequence ^ randomBits12) & 0x0FFFL);
        long randomBits = random.nextLong();
        long lsb = (randomBits & 0x3FFFFFFFFFFFFFFFL) | 0x8000000000000000L;
        return new UUID(msb, lsb);
    }

    public static UUID randomUUID() {
        return SHARED.generate();
    }
}
