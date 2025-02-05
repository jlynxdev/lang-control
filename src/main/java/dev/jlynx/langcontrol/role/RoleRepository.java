package dev.jlynx.langcontrol.role;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on {@link Role} entities.
 *
 * <p>Extends {@link ListCrudRepository} to provide generic CRUD functionality.
 * Includes a method to find a {@link Role} by its string value.
 *
 * @see Role
 */
@Repository
public interface RoleRepository extends ListCrudRepository<Role, Long> {

    /**
     * Finds a {@link Role} by its string value (one of the {@link DefinedRoleValue} values).
     *
     * @param value the string value of the role to be retrieved.
     * @return an {@link Optional} containing the {@link Role} if found, or an empty {@link Optional} if not.
     */
    Optional<Role> findByValue(String value);
}
