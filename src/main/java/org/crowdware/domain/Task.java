package org.crowdware.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A Task.
 */
@Entity
@Table(name = "task")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "task")
public class Task implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "app")
    private Long app;

    @NotNull
    @Column(name = "job", nullable = false)
    private Long job;

    @NotNull
    @Column(name = "start", nullable = false)
    private ZonedDateTime start;

    @NotNull
    @Column(name = "end", nullable = false)
    private ZonedDateTime end;

    @ManyToOne
    private App app_con;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApp() {
        return app;
    }

    public void setApp(Long app) {
        this.app = app;
    }

    public Long getJob() {
        return job;
    }

    public void setJob(Long job) {
        this.job = job;
    }

    public ZonedDateTime getStart() {
        return start;
    }

    public void setStart(ZonedDateTime start) {
        this.start = start;
    }

    public ZonedDateTime getEnd() {
        return end;
    }

    public void setEnd(ZonedDateTime end) {
        this.end = end;
    }

    public App getApp_con() {
        return app_con;
    }

    public void setApp_con(App app) {
        this.app_con = app;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Task task = (Task) o;
        if(task.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Task{" +
            "id=" + id +
            ", app='" + app + "'" +
            ", job='" + job + "'" +
            ", start='" + start + "'" +
            ", end='" + end + "'" +
            '}';
    }
}
