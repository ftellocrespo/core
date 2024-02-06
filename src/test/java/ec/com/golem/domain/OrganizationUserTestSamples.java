package ec.com.golem.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class OrganizationUserTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static OrganizationUser getOrganizationUserSample1() {
        return new OrganizationUser().id(1L);
    }

    public static OrganizationUser getOrganizationUserSample2() {
        return new OrganizationUser().id(2L);
    }

    public static OrganizationUser getOrganizationUserRandomSampleGenerator() {
        return new OrganizationUser().id(longCount.incrementAndGet());
    }
}
