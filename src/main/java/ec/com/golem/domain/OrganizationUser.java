package ec.com.golem.domain;

import ec.com.golem.domain.enumeration.AuthStateEnum;
import ec.com.golem.domain.enumeration.RoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The OrganizationUser entity.
 */
@Schema(description = "The OrganizationUser entity.")
@Entity
@Table(name = "organization_user")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "organizationuser")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrganizationUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * The state attribute.
     */
    @Schema(description = "The state attribute.")
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private AuthStateEnum state;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private RoleEnum role;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public OrganizationUser id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AuthStateEnum getState() {
        return this.state;
    }

    public OrganizationUser state(AuthStateEnum state) {
        this.setState(state);
        return this;
    }

    public void setState(AuthStateEnum state) {
        this.state = state;
    }

    public RoleEnum getRole() {
        return this.role;
    }

    public OrganizationUser role(RoleEnum role) {
        this.setRole(role);
        return this;
    }

    public void setRole(RoleEnum role) {
        this.role = role;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrganizationUser)) {
            return false;
        }
        return getId() != null && getId().equals(((OrganizationUser) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrganizationUser{" +
            "id=" + getId() +
            ", state='" + getState() + "'" +
            ", role='" + getRole() + "'" +
            "}";
    }
}
