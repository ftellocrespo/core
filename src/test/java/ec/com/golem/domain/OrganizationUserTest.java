package ec.com.golem.domain;

import static ec.com.golem.domain.OrganizationUserTestSamples.*;
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
}
