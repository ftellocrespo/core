package ec.com.golem.domain;

import static ec.com.golem.domain.ErrorLogTestSamples.*;
import static ec.com.golem.domain.OrganizationUserTestSamples.*;
import static ec.com.golem.domain.UserXTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ec.com.golem.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
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

    @Test
    void organizationUserTest() throws Exception {
        UserX userX = getUserXRandomSampleGenerator();
        OrganizationUser organizationUserBack = getOrganizationUserRandomSampleGenerator();

        userX.addOrganizationUser(organizationUserBack);
        assertThat(userX.getOrganizationUsers()).containsOnly(organizationUserBack);
        assertThat(organizationUserBack.getUser()).isEqualTo(userX);

        userX.removeOrganizationUser(organizationUserBack);
        assertThat(userX.getOrganizationUsers()).doesNotContain(organizationUserBack);
        assertThat(organizationUserBack.getUser()).isNull();

        userX.organizationUsers(new HashSet<>(Set.of(organizationUserBack)));
        assertThat(userX.getOrganizationUsers()).containsOnly(organizationUserBack);
        assertThat(organizationUserBack.getUser()).isEqualTo(userX);

        userX.setOrganizationUsers(new HashSet<>());
        assertThat(userX.getOrganizationUsers()).doesNotContain(organizationUserBack);
        assertThat(organizationUserBack.getUser()).isNull();
    }

    @Test
    void errorLogTest() throws Exception {
        UserX userX = getUserXRandomSampleGenerator();
        ErrorLog errorLogBack = getErrorLogRandomSampleGenerator();

        userX.addErrorLog(errorLogBack);
        assertThat(userX.getErrorLogs()).containsOnly(errorLogBack);
        assertThat(errorLogBack.getUser()).isEqualTo(userX);

        userX.removeErrorLog(errorLogBack);
        assertThat(userX.getErrorLogs()).doesNotContain(errorLogBack);
        assertThat(errorLogBack.getUser()).isNull();

        userX.errorLogs(new HashSet<>(Set.of(errorLogBack)));
        assertThat(userX.getErrorLogs()).containsOnly(errorLogBack);
        assertThat(errorLogBack.getUser()).isEqualTo(userX);

        userX.setErrorLogs(new HashSet<>());
        assertThat(userX.getErrorLogs()).doesNotContain(errorLogBack);
        assertThat(errorLogBack.getUser()).isNull();
    }
}
