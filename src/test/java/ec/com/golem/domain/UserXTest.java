package ec.com.golem.domain;

import static ec.com.golem.domain.UserXTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ec.com.golem.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserXTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserX.class);
        UserX userX1 = getUserXSample1();
        UserX userX2 = new UserX();
        assertThat(userX1).isNotEqualTo(userX2);

        userX2.setId(userX1.getId());
        assertThat(userX1).isEqualTo(userX2);

        userX2 = getUserXSample2();
        assertThat(userX1).isNotEqualTo(userX2);
    }
}
