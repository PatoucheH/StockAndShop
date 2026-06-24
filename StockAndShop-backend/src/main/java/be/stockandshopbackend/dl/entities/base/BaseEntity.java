package be.stockandshopbackend.dl.entities.base;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Root mapped superclass for all entities. Provides automatic {@code createdAt}/{@code updatedAt}
 * timestamps via JPA lifecycle hooks and identity-based {@code equals}/{@code hashCode} so
 * Hibernate collection comparisons work correctly across proxy boundaries.
 */
@MappedSuperclass
@Getter @Setter
public abstract class BaseEntity<T> {

    public abstract T getId();

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity<?> that = (BaseEntity<?>) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
