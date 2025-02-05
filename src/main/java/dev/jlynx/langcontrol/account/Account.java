package dev.jlynx.langcontrol.account;

import jakarta.persistence.*;
import dev.jlynx.langcontrol.role.Role;
import dev.jlynx.langcontrol.userprofile.UserProfile;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


/**
 * Represents an account in the system, which is linked to a {@link UserProfile}
 * and is responsible for handling authentication and authorisation using Spring Security's
 * {@link UserDetails} interface.
 * <p>
 * This class provides:
 * <ul>
 *     <li>A unique identifier {@code id} for each account</li>
 *     <li>The username and password fields for authentication</li>
 *     <li>Associations with {@link Role} entities to define the account's roles and authorities</li>
 *     <li>Status fields to manage account expiration, locking, credential validity, and enablement</li>
 *     <li>An association with the {@link UserProfile} entity for user-related information</li>
 * </ul>
 * <p>
 * The {@code Account} class is designed to integrate with Spring Security by implementing
 * the {@link UserDetails} interface.
 *
 * <p><b>Entity Mapping:</b></p>
 * <ul>
 *     <li>{@code @Entity}: Marks the class as a JPA entity.</li>
 *     <li>{@code @Table(name = "account")}: Specifies the database table name for this entity.</li>
 *     <li>{@code @Id} and {@code @GeneratedValue}: Denotes the primary key and its generation strategy.</li>
 *     <li>{@code @Column}: Specifies unique and non-null constraints for relevant columns.</li>
 *     <li>{@code @ManyToMany}: Maps the relationship between {@code Account} and {@code Role} entities.</li>
 *     <li>{@code @OneToOne}: Maps a one-to-one relationship between {@code Account} and {@code UserProfile}.</li>
 * </ul>
 *
 * <p><b>Spring Security Integration:</b></p>
 * Implements the {@link UserDetails} methods:
 * <ul>
 *     <li>{@link #getAuthorities()}: Retrieves the account's roles as granted authorities.</li>
 *     <li>{@code isAccountNonExpired()}, {@code isAccountNonLocked()}, etc.: Indicate account status conditions.</li>
 * </ul>
 */
@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name = "account")
public class Account implements UserDetails {

    /**
     * Represents the primary key of the {@link Account} entity.
     * <p>
     * The {@code id} field is automatically generated using the
     * {@code GenerationType.IDENTITY} strategy, ensuring each {@link Account}
     * instance has a unique identifier.
     * <p>
     * This field is marked with the {@code @Id} annotation to indicate
     * it is the primary key in the corresponding database table.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Represents the unique username associated with a user.
     * <p>
     * This field is mandatory as it cannot be null and must be unique across all records.
     * It is used for identifying a user within the system.
     *
     * @see String
     */
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * Represents the password for a user or entity.
     * <p>
     * This field is required and cannot be {@code null}.
     * <ul>
     *   <li>Stored as a {@code String} in the database</li>
     *   <li>Marked with {@link Column} annotation to enforce non-null constraint</li>
     * </ul>
     * Use secure practices for managing and accessing this field to protect sensitive information.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Represents the roles associated with an account in a many-to-many relationship.
     * <ul>
     *   <li>Uses {@code FetchType.EAGER} to fetch roles when the account is retrieved.</li>
     *   <li>Mapped to the {@code account_role} join table with {@code account_id} as the join column
     *       and {@code role_id} as the inverse join column.</li>
     *   <li>Defines foreign key constraints using {@code fk_account_role} and {@code fk_role_account}.</li>
     * </ul>
     *
     * @see Role
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "account_role",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"),
            foreignKey = @ForeignKey(name = "fk_account_role"),
            inverseForeignKey = @ForeignKey(name = "fk_role_account"))
    private List<Role> roles;

    /**
     * Indicates whether the account is non-expired.
     * <p>
     * If {@code true}, the account is active and not expired, allowing the user to authenticate.
     * If {@code false}, the account is expired and access is restricted.
     */
    @Column(name = "account_non_expired", nullable = false)
    private boolean accountNonExpired;

    /**
     * Indicates whether the account is non-locked.
     * <p>
     * A value of {@code true} signifies that the account is not locked
     * and is allowed to access the system. A value of {@code false}
     * indicates that the account is locked and access is restricted.
     * <p>
     * Mapped to the {@code account_non_locked} column in the database
     * and cannot be null.
     */
    @Column(name = "account_non_locked", nullable = false)
    private boolean accountNonLocked;

    /**
     * Indicates whether the credentials of the user are non-expired.
     * <p>
     * This variable is used to determine if the user's credentials are still valid.
     * If {@code true}, the credentials are valid and can be used for authentication.
     * If {@code false}, the credentials have expired and need to be updated.
     */
    @Column(name = "credentials_non_expired", nullable = false)
    private boolean credentialsNonExpired;

    /**
     * Indicates whether an entity or feature is enabled.
     * <p>
     * This field is represented as a {@code boolean} and cannot be null.
     * A {@code true} value signifies that the entity or feature is enabled
     * while a {@code false} value signifies that it is disabled.
     * </p>
     */
    @Column(nullable = false)
    private boolean enabled;

    /**
     * Represents the {@link UserProfile} associated with the user account.
     *
     * <p>
     * This field is mapped as a one-to-one relationship with cascading operations and orphan removal enabled.
     * It uses eager fetching to load the associated {@code UserProfile} immediately.
     *
     * <ul>
     * <li>The database column is named {@code user_profile_id}.
     * <li>A foreign key constraint {@code fk_account_user_profile} is applied.
     * </ul>
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_profile_id", foreignKey = @ForeignKey(name = "fk_account_user_profile"))
    private UserProfile userProfile;

    public Account(Long id, String username, String password, List<Role> roles, boolean accountNonExpired,
                   boolean accountNonLocked, boolean credentialsNonExpired, boolean enabled) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
    }

    public Account(Long id, String username, String password, List<Role> roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.accountNonExpired = true;
        this.accountNonLocked = true;
        this.credentialsNonExpired = true;
        this.enabled = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }
}
