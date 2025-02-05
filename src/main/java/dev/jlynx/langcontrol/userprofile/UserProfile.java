package dev.jlynx.langcontrol.userprofile;

import jakarta.persistence.*;
import dev.jlynx.langcontrol.deck.Deck;
import dev.jlynx.langcontrol.account.Account;
import dev.jlynx.langcontrol.usersettings.UserSettings;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

/**
 * Represents a user's profile within the application. This entity is linked to {@link Account},
 * {@link Deck}, and {@link UserSettings}.
 *
 * @see Account
 */
@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name = "user_profile")
public class UserProfile {

    /**
     * Represents the primary identifier for the {@link UserProfile} entity.
     *
     * <p>This field is auto-generated using the {@link GenerationType#IDENTITY} strategy
     * and serves as the unique key for database operations.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Represents the first name of the user associated with the {@link UserProfile}.
     * This field is mandatory and mapped to the {@code first_name} column in the database.
     */
    @Column(name="first_name", nullable = false)
    private String firstName;

    /**
     * Represents the {@link Account} associated with the user's profile.
     * It is linked via a bidirectional one-to-one relationship where this
     * variable is mapped by the {@code userProfile} field in the {@link Account} entity.
     */
    @OneToOne(mappedBy = "userProfile")
    private Account account;

    /**
     * Represents the collection of {@link Deck} entities associated with a {@link UserProfile}.
     *
     * <p>
     * Each deck belongs to the user's profile and is managed with cascading operations,
     * ensuring changes to the {@link UserProfile} propagate to its associated {@link Deck} entities.
     * The relationship utilises {@code LAZY} fetching and supports orphan removal.
     * </p>
     *
     * @see Deck
     */
    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Deck> decks;

    /**
     * Represents the user's settings associated with the {@link UserProfile}.
     * This entity is managed with a one-to-one relationship, ensuring each
     * {@link UserProfile} is linked to a unique {@link UserSettings}.
     * <p>
     * The association uses cascading for all operations, including removal
     * of orphaned entities, and is lazily fetched to optimise performance.
     * <p>
     * The database column uses {@code user_settings_id} as its join column and
     * enforces a foreign key constraint named {@code fk_user_profile_user_settings}.
     *
     * @see UserSettings
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_settings_id", foreignKey = @ForeignKey(name = "fk_user_profile_user_settings"))
    private UserSettings userSettings;

    public UserProfile(Long id, String firstName) {
        this.id = id;
        this.firstName = firstName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfile that = (UserProfile) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
