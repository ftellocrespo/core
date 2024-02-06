package ec.com.golem.domain;

import static ec.com.golem.domain.OrganizationTestSamples.*;
import static ec.com.golem.domain.OrganizationUserTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ec.com.golem.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class OrganizationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Organization.class);
        Organization organization1 = getOrganizationSample1();
        Organization organization2 = new Organization();
        assertThat(organization1).isNotEqualTo(organization2);

        organization2.setId(organization1.getId());
        assertThat(organization1).isEqualTo(organization2);

        organization2 = getOrganizationSample2();
        assertThat(organization1).isNotEqualTo(organization2);
    }

    @Test
    void organizationUserTest() throws Exception {
        Organization organization = getOrganizationRandomSampleGenerator();
        OrganizationUser organizationUserBack = getOrganizationUserRandomSampleGenerator();

        organization.addOrganizationUser(organizationUserBack);
        assertThat(organization.getOrganizationUsers()).containsOnly(organizationUserBack);
        assertThat(organizationUserBack.getOrganization()).isEqualTo(organization);

        organization.removeOrganizationUser(organizationUserBack);
        assertThat(organization.getOrganizationUsers()).doesNotContain(organizationUserBack);
        assertThat(organizationUserBack.getOrganization()).isNull();

        organization.organizationUsers(new HashSet<>(Set.of(organizationUserBack)));
        assertThat(organization.getOrganizationUsers()).containsOnly(organizationUserBack);
        assertThat(organizationUserBack.getOrganization()).isEqualTo(organization);

        organization.setOrganizationUsers(new HashSet<>());
        assertThat(organization.getOrganizationUsers()).doesNotContain(organizationUserBack);
        assertThat(organizationUserBack.getOrganization()).isNull();
    }
}
