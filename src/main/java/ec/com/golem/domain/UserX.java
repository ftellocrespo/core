package ec.com.golem.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ec.com.golem.domain.enumeration.AuthStateEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * User entity.
 * @author fred.
 */
@Schema(description = "User entity.\n@author fred.")
@Entity
@Table(name = "user_x")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "userx")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserX implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "email", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String email;

    @NotNull
    @Column(name = "password", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String password;

    @Column(name = "facebook_id")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String facebookId;

    @Column(name = "google_id")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String googleId;

    @Column(name = "first_name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String firstName;

    @Column(name = "last_name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String lastName;

    @Column(name = "phone")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String phone;

    @Column(name = "birth")
    private LocalDate birth;

    @Column(name = "gender")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String gender;

    @Column(name = "nationality")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String nationality;

    @Column(name = "address")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String address;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private AuthStateEnum state;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "organization", "user" }, allowSetters = true)
    private Set<OrganizationUser> organizationUsers = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private Set<ErrorLog> errorLogs = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserX id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return this.email;
    }

    public UserX email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public UserX password(String password) {
        this.setPassword(password);
        return this;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFacebookId() {
        return this.facebookId;
    }

    public UserX facebookId(String facebookId) {
        this.setFacebookId(facebookId);
        return this;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getGoogleId() {
        return this.googleId;
    }

    public UserX googleId(String googleId) {
        this.setGoogleId(googleId);
        return this;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public UserX firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public UserX lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return this.phone;
    }

    public UserX phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getBirth() {
        return this.birth;
    }

    public UserX birth(LocalDate birth) {
        this.setBirth(birth);
        return this;
    }

    public void setBirth(LocalDate birth) {
        this.birth = birth;
    }

    public String getGender() {
        return this.gender;
    }

    public UserX gender(String gender) {
        this.setGender(gender);
        return this;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNationality() {
        return this.nationality;
    }

    public UserX nationality(String nationality) {
        this.setNationality(nationality);
        return this;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getAddress() {
        return this.address;
    }

    public UserX address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public AuthStateEnum getState() {
        return this.state;
    }

    public UserX state(AuthStateEnum state) {
        this.setState(state);
        return this;
    }

    public void setState(AuthStateEnum state) {
        this.state = state;
    }

    public Set<OrganizationUser> getOrganizationUsers() {
        return this.organizationUsers;
    }

    public void setOrganizationUsers(Set<OrganizationUser> organizationUsers) {
        if (this.organizationUsers != null) {
            this.organizationUsers.forEach(i -> i.setUser(null));
        }
        if (organizationUsers != null) {
            organizationUsers.forEach(i -> i.setUser(this));
        }
        this.organizationUsers = organizationUsers;
    }

    public UserX organizationUsers(Set<OrganizationUser> organizationUsers) {
        this.setOrganizationUsers(organizationUsers);
        return this;
    }

    public UserX addOrganizationUser(OrganizationUser organizationUser) {
        this.organizationUsers.add(organizationUser);
        organizationUser.setUser(this);
        return this;
    }

    public UserX removeOrganizationUser(OrganizationUser organizationUser) {
        this.organizationUsers.remove(organizationUser);
        organizationUser.setUser(null);
        return this;
    }

    public Set<ErrorLog> getErrorLogs() {
        return this.errorLogs;
    }

    public void setErrorLogs(Set<ErrorLog> errorLogs) {
        if (this.errorLogs != null) {
            this.errorLogs.forEach(i -> i.setUser(null));
        }
        if (errorLogs != null) {
            errorLogs.forEach(i -> i.setUser(this));
        }
        this.errorLogs = errorLogs;
    }

    public UserX errorLogs(Set<ErrorLog> errorLogs) {
        this.setErrorLogs(errorLogs);
        return this;
    }

    public UserX addErrorLog(ErrorLog errorLog) {
        this.errorLogs.add(errorLog);
        errorLog.setUser(this);
        return this;
    }

    public UserX removeErrorLog(ErrorLog errorLog) {
        this.errorLogs.remove(errorLog);
        errorLog.setUser(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserX)) {
            return false;
        }
        return getId() != null && getId().equals(((UserX) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserX{" +
            "id=" + getId() +
            ", email='" + getEmail() + "'" +
            ", password='" + getPassword() + "'" +
            ", facebookId='" + getFacebookId() + "'" +
            ", googleId='" + getGoogleId() + "'" +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", phone='" + getPhone() + "'" +
            ", birth='" + getBirth() + "'" +
            ", gender='" + getGender() + "'" +
            ", nationality='" + getNationality() + "'" +
            ", address='" + getAddress() + "'" +
            ", state='" + getState() + "'" +
            "}";
    }
}
