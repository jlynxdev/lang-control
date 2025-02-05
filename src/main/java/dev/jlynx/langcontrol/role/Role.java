package dev.jlynx.langcontrol.role;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

/**
 * Represents a role entity assigned to {@link dev.jlynx.langcontrol.account.Account} for access control.
 *
 * <p>This class is mapped to the {@code role} database table and serves as an
 * implementation of the {@link GrantedAuthority} interface. Each instance encapsulates
 * the details of a specific role, including its unique identifier and string value.
 * <p>
 * This entity provides the authority string via the {@link #getAuthority()} method, which
 * facilitates role-based security within the system.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "role")
public class Role implements GrantedAuthority {

    /**
     * Represents the unique identifier of the {@link Role} entity.
     *
     * <p>
     * This field is the primary key of the {@code role} table in the database. It is
     * auto-generated using the {@link GenerationType#IDENTITY} strategy.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Represents the string value of a role, corresponding to its authority within the system.
     * It is expected to use one of the {@link DefinedRoleValue} values.
     *
     * <p>
     * This field is mapped to the {@code string_value} column in the {@code role} database table.
     * It defines the unique textual identifier for each role and is utilised by {@link #getAuthority()}
     * to provide the role's authority representation.
     * </p>
     */
    @Column(name = "string_value")
    private String value;

    /**
     * Constructs a new {@link Role} instance with the specified ID and {@link DefinedRoleValue}.
     *
     * @param id               the unique identifier of the role.
     * @param definedRoleValue the predefined role value used to initialise the role's string value.
     */
    public Role(Long id, DefinedRoleValue definedRoleValue) {
        this.id = id;
        this.value = definedRoleValue.getValue();
    }

    /**
     * Retrieves the authority string associated with this {@link Role}.
     *
     * <p>
     * This method is an implementation of the {@link GrantedAuthority#getAuthority()} method,
     * returning the value of the role as its authority representation.
     * </p>
     *
     * @return the authority string of this role, corresponding to the value provided by {@link #getValue()}.
     */
    @Override
    public String getAuthority() {
        return this.getValue();
    }
}
