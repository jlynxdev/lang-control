package dev.jlynx.langcontrol.usersettings;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link UserSettings} entities.
 * <p>
 * Extends {@link ListCrudRepository} to provide basic
 * CRUD operations and additional list-based methods for the {@code UserSettings} entity.
 * This repository manages the persistence of user-specific settings data in the database.
 *
 * @see UserSettings
 */
@Repository
public interface UserSettingsRepository extends ListCrudRepository<UserSettings, Long> {
}