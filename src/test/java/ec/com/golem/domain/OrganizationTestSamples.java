package ec.com.golem.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class OrganizationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Organization getOrganizationSample1() {
        return new Organization()
            .id(1L)
            .identification("identification1")
            .name("name1")
            .address("address1")
            .phone("phone1")
            .email("email1");
    }

    public static Organization getOrganizationSample2() {
        return new Organization()
            .id(2L)
            .identification("identification2")
            .name("name2")
            .address("address2")
            .phone("phone2")
            .email("email2");
    }

    public static Organization getOrganizationRandomSampleGenerator() {
        return new Organization()
            .id(longCount.incrementAndGet())
            .identification(UUID.randomUUID().toString())
            .name(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString());
    }
}
