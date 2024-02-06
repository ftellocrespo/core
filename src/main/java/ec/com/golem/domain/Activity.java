package ec.com.golem.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ec.com.golem.domain.enumeration.ActivityTypeEnum;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Activity.
 */
@Entity
@Table(name = "activity")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "activity")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Activity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private ActivityTypeEnum type;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "activity")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "activity" }, allowSetters = true)
    private Set<Action> actions = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "activities" }, allowSetters = true)
    private Process process;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Activity id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ActivityTypeEnum getType() {
        return this.type;
    }

    public Activity type(ActivityTypeEnum type) {
        this.setType(type);
        return this;
    }

    public void setType(ActivityTypeEnum type) {
        this.type = type;
    }

    public Set<Action> getActions() {
        return this.actions;
    }

    public void setActions(Set<Action> actions) {
        if (this.actions != null) {
            this.actions.forEach(i -> i.setActivity(null));
        }
        if (actions != null) {
            actions.forEach(i -> i.setActivity(this));
        }
        this.actions = actions;
    }

    public Activity actions(Set<Action> actions) {
        this.setActions(actions);
        return this;
    }

    public Activity addAction(Action action) {
        this.actions.add(action);
        action.setActivity(this);
        return this;
    }

    public Activity removeAction(Action action) {
        this.actions.remove(action);
        action.setActivity(null);
        return this;
    }

    public Process getProcess() {
        return this.process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public Activity process(Process process) {
        this.setProcess(process);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Activity)) {
            return false;
        }
        return getId() != null && getId().equals(((Activity) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Activity{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            "}";
    }
}
