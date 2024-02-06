package ec.com.golem.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ec.com.golem.domain.enumeration.AuthStateEnum;
import ec.com.golem.domain.enumeration.CountryEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Organization.
 */
@Entity
@Table(name = "organization")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "organization")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Organization implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "identification", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String identification;

    @NotNull
    @Column(name = "name", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private AuthStateEnum state;

    @Enumerated(EnumType.STRING)
    @Column(name = "country")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private CountryEnum country;

    @Column(name = "address")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String address;

    @Column(name = "phone")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String phone;

    @Column(name = "email")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String email;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "organization")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "user" }, allowSetters = true)
    private Set<OrganizationUser> organizationUsers = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Organization id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentification() {
        return this.identification;
    }

    public Organization identification(String identification) {
        this.setIdentification(identification);
        return this;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getName() {
        return this.name;
    }

    public Organization name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AuthStateEnum getState() {
        return this.state;
    }

    public Organization state(AuthStateEnum state) {
        this.setState(state);
        return this;
    }

    public void setState(AuthStateEnum state) {
        this.state = state;
    }

    public CountryEnum getCountry() {
        return this.country;
    }

    public Organization country(CountryEnum country) {
        this.setCountry(country);
        return this;
    }

    public void setCountry(CountryEnum country) {
        this.country = country;
    }

    public String getAddress() {
        return this.address;
    }

    public Organization address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return this.phone;
    }

    public Organization phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return this.email;
    }

    public Organization email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<OrganizationUser> getOrganizationUsers() {
        return this.organizationUsers;
    }

    public void setOrganizationUsers(Set<OrganizationUser> organizationUsers) {
        if (this.organizationUsers != null) {
            this.organizationUsers.forEach(i -> i.setOrganization(null));
        }
        if (organizationUsers != null) {
            organizationUsers.forEach(i -> i.setOrganization(this));
        }
        this.organizationUsers = organizationUsers;
    }

    public Organization organizationUsers(Set<OrganizationUser> organizationUsers) {
        this.setOrganizationUsers(organizationUsers);
        return this;
    }

    public Organization addOrganizationUser(OrganizationUser organizationUser) {
        this.organizationUsers.add(organizationUser);
        organizationUser.setOrganization(this);
        return this;
    }

    public Organization removeOrganizationUser(OrganizationUser organizationUser) {
        this.organizationUsers.remove(organizationUser);
        organizationUser.setOrganization(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Organization)) {
            return false;
        }
        return getId() != null && getId().equals(((Organization) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Organization{" +
            "id=" + getId() +
            ", identification='" + getIdentification() + "'" +
            ", name='" + getName() + "'" +
            ", state='" + getState() + "'" +
            ", country='" + getCountry() + "'" +
            ", address='" + getAddress() + "'" +
            ", phone='" + getPhone() + "'" +
            ", email='" + getEmail() + "'" +
            "}";
    }
}
