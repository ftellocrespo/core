package ec.com.golem.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ErrorLogTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ErrorLog getErrorLogSample1() {
        return new ErrorLog().id(1L).className("className1").line(1L).error("error1").erroyType("erroyType1");
    }

    public static ErrorLog getErrorLogSample2() {
        return new ErrorLog().id(2L).className("className2").line(2L).error("error2").erroyType("erroyType2");
    }

    public static ErrorLog getErrorLogRandomSampleGenerator() {
        return new ErrorLog()
            .id(longCount.incrementAndGet())
            .className(UUID.randomUUID().toString())
            .line(longCount.incrementAndGet())
            .error(UUID.randomUUID().toString())
            .erroyType(UUID.randomUUID().toString());
    }
}
