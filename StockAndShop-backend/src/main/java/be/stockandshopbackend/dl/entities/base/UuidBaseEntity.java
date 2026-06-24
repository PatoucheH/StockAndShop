package be.stockandshopbackend.dl.entities.base;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Base entity variant that uses a database-generated UUID as primary key.
 * Used for publicly exposed aggregates (Home, User, Recipe) where auto-increment
 * integers would leak row-count information.
 */
@MappedSuperclass
@Getter @Setter
public abstract class UuidBaseEntity extends BaseEntity<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
}
