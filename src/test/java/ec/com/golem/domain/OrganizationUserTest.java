package ec.com.golem.domain;

import static ec.com.golem.domain.OrganizationTestSamples.*;
import static ec.com.golem.domain.OrganizationUserTestSamples.*;
import static ec.com.golem.domain.UserXTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ec.com.golem.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrganizationUserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrganizationUser.class);
        OrganizationUser organizationUser1 = getOrganizationUserSample1();
        OrganizationUser organizationUser2 = new OrganizationUser();
        assertThat(organizationUser1).isNotEqualTo(organizationUser2);

        organizationUser2.setId(organizationUser1.getId());
        assertThat(organizationUser1).isEqualTo(organizationUser2);

        organizationUser2 = getOrganizationUserSample2();
        assertThat(organizationUser1).isNotEqualTo(organizationUser2);
    }

    @Test
    void organizationTest() throws Exception {
        OrganizationUser organizationUser = getOrganizationUserRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        organizationUser.setOrganization(organizationBack);
        assertThat(organizationUser.getOrganization()).isEqualTo(organizationBack);

        organizationUser.organization(null);
        assertThat(organizationUser.getOrganization()).isNull();
    }

    @Test
    void userTest() throws Exception {
        OrganizationUser organizationUser = getOrganizationUserRandomSampleGenerator();
        UserX userXBack = getUserXRandomSampleGenerator();

        organizationUser.setUser(userXBack);
        assertThat(organizationUser.getUser()).isEqualTo(userXBack);

        organizationUser.user(null);
        assertThat(organizationUser.getUser()).isNull();
    }
}
