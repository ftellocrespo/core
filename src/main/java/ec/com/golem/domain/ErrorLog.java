package ec.com.golem.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ErrorLog.
 */
@Entity
@Table(name = "error_log")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "errorlog")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ErrorLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "class_name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String className;

    @Column(name = "line")
    private Long line;

    @Column(name = "error")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String error;

    @Column(name = "erroy_type")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String erroyType;

    @Column(name = "timestamp")
    private LocalDate timestamp;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ErrorLog id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassName() {
        return this.className;
    }

    public ErrorLog className(String className) {
        this.setClassName(className);
        return this;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Long getLine() {
        return this.line;
    }

    public ErrorLog line(Long line) {
        this.setLine(line);
        return this;
    }

    public void setLine(Long line) {
        this.line = line;
    }

    public String getError() {
        return this.error;
    }

    public ErrorLog error(String error) {
        this.setError(error);
        return this;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErroyType() {
        return this.erroyType;
    }

    public ErrorLog erroyType(String erroyType) {
        this.setErroyType(erroyType);
        return this;
    }

    public void setErroyType(String erroyType) {
        this.erroyType = erroyType;
    }

    public LocalDate getTimestamp() {
        return this.timestamp;
    }

    public ErrorLog timestamp(LocalDate timestamp) {
        this.setTimestamp(timestamp);
        return this;
    }

    public void setTimestamp(LocalDate timestamp) {
        this.timestamp = timestamp;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ErrorLog)) {
            return false;
        }
        return getId() != null && getId().equals(((ErrorLog) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ErrorLog{" +
            "id=" + getId() +
            ", className='" + getClassName() + "'" +
            ", line=" + getLine() +
            ", error='" + getError() + "'" +
            ", erroyType='" + getErroyType() + "'" +
            ", timestamp='" + getTimestamp() + "'" +
            "}";
    }
}
