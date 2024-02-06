package ec.com.golem.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UserXTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static UserX getUserXSample1() {
        return new UserX()
            .id(1L)
            .email("email1")
            .password("password1")
            .facebookId("facebookId1")
            .googleId("googleId1")
            .firstName("firstName1")
            .lastName("lastName1")
            .phone("phone1")
            .gender("gender1")
            .nationality("nationality1")
            .address("address1");
    }

    public static UserX getUserXSample2() {
        return new UserX()
            .id(2L)
            .email("email2")
            .password("password2")
            .facebookId("facebookId2")
            .googleId("googleId2")
            .firstName("firstName2")
            .lastName("lastName2")
            .phone("phone2")
            .gender("gender2")
            .nationality("nationality2")
            .address("address2");
    }

    public static UserX getUserXRandomSampleGenerator() {
        return new UserX()
            .id(longCount.incrementAndGet())
            .email(UUID.randomUUID().toString())
            .password(UUID.randomUUID().toString())
            .facebookId(UUID.randomUUID().toString())
            .googleId(UUID.randomUUID().toString())
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .gender(UUID.randomUUID().toString())
            .nationality(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString());
    }
}
