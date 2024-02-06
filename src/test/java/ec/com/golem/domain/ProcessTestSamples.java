package ec.com.golem.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProcessTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Process getProcessSample1() {
        return new Process().id(1L).name("name1").meta("meta1");
    }

    public static Process getProcessSample2() {
        return new Process().id(2L).name("name2").meta("meta2");
    }

    public static Process getProcessRandomSampleGenerator() {
        return new Process().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).meta(UUID.randomUUID().toString());
    }
}
