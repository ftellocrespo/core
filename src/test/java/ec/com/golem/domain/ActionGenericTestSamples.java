package ec.com.golem.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ActionGenericTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ActionGeneric getActionGenericSample1() {
        return new ActionGeneric().id(1L).message("message1");
    }

    public static ActionGeneric getActionGenericSample2() {
        return new ActionGeneric().id(2L).message("message2");
    }

    public static ActionGeneric getActionGenericRandomSampleGenerator() {
        return new ActionGeneric().id(longCount.incrementAndGet()).message(UUID.randomUUID().toString());
    }
}
