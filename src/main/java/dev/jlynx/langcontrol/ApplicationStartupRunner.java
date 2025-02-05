package dev.jlynx.langcontrol;

import dev.jlynx.langcontrol.account.Account;
import dev.jlynx.langcontrol.account.AccountRepository;
import dev.jlynx.langcontrol.role.DefinedRoleValue;
import dev.jlynx.langcontrol.role.Role;
import dev.jlynx.langcontrol.role.RoleRepository;
import dev.jlynx.langcontrol.userprofile.UserProfile;
import dev.jlynx.langcontrol.usersettings.UserSettings;
import dev.jlynx.langcontrol.usersettings.UserSettingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A Spring {@link Component} that executes at application startup, implementing
 * the {@link ApplicationRunner} interface.
 *
 * <p>This class ensures the initial setup of the application by creating required
 * roles and an admin account if they do not already exist. Configuration properties
 * for the admin account are injected from the environment variables using {@code @Value}.
 *
 * <p>Dependencies:
 * <ul>
 *   <li>{@link RoleRepository} to manage roles.</li>
 *   <li>{@link UserSettingsRepository} to manage user settings.</li>
 *   <li>{@link AccountRepository} to manage user accounts.</li>
 *   <li>{@link PasswordEncoder} to encode the admin password.</li>
 * </ul>
 */
@Component
public class ApplicationStartupRunner implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationStartupRunner.class);

    /**
     * Stores the username of the admin account, retrieved from the
     * external configuration property {@code admin.properties.username}.
     *
     * <p>Used during application startup by {@link ApplicationStartupRunner}
     * to verify the existence of the admin account or to initialise it if necessary.
     */
    @Value("${admin.properties.username}")
    private String adminUsername;

    /**
     * Holds the password for the admin account used during application startup.
     * <p>
     * Configured via the {@code admin.properties.password} property in the application's
     * configuration files. This value is used to create or verify the admin account
     * upon initialisation.
     */
    @Value("${admin.properties.password}")
    private String adminPassword;

    private final RoleRepository roleRepository;
    private final UserSettingsRepository userSettingsRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder encoder;

    @Autowired
    public ApplicationStartupRunner(
            RoleRepository roleRepository,
            UserSettingsRepository userSettingsRepository,
            AccountRepository accountRepository,
            PasswordEncoder encoder
    ) {
        this.roleRepository = roleRepository;
        this.userSettingsRepository = userSettingsRepository;
        this.accountRepository = accountRepository;
        this.encoder = encoder;
    }

    /**
     * Executes logic during application startup to ensure the admin account and roles are created.
     * If an admin account already exists, no further processing occurs.
     *
     * <p>The method performs the following actions:
     * <ul>
     *   <li>Checks whether an admin account with {@code adminUsername} exists
     *   using {@link AccountRepository#existsByUsername}.</li>
     *   <li>Ensures that {@code ADMIN} and {@code USER} roles exist in the database,
     *   creating them if necessary using {@link RoleRepository#findByValue}.</li>
     *   <li>Creates a new admin {@link Account} with the specified {@code adminUsername} and {@code adminPassword},
     *   linking it to the required roles, a {@link UserProfile} and {@link UserSettings}.</li>
     *   <li>Saves the constructed {@code Account}, {@code UserProfile} and {@code UserSettings} instances
     *   to the database.</li>
     * </ul>
     *
     * @param args encapsulates command-line arguments passed to the application
     *             (unused in this implementation, but must not be {@code null}).
     * @throws Exception if any unexpected issue occurs during execution.
     */
    @Transactional
    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (accountRepository.existsByUsername(adminUsername)) {
            LOG.debug("Admin account with username='root' not created (already exists).");
            return;
        }

        Optional<Role> adminRoleOptional = roleRepository.findByValue(DefinedRoleValue.ADMIN.getValue());
        Role adminRole;
        if (adminRoleOptional.isEmpty()) {
            Role newAdminRole = new Role(null, DefinedRoleValue.ADMIN);
            adminRole = roleRepository.save(newAdminRole);
        } else {
            adminRole = adminRoleOptional.get();
        }
        Optional<Role> userRoleOptional = roleRepository.findByValue(DefinedRoleValue.USER.getValue());
        Role userRole;
        if (userRoleOptional.isEmpty()) {
            Role newAdminRole = new Role(null, DefinedRoleValue.USER);
            userRole = roleRepository.save(newAdminRole);
        } else {
            userRole = userRoleOptional.get();
        }
        List<Role> roles = List.of(adminRole, userRole);

        Account accountToCreate = new Account(
                null,
                adminUsername,
                encoder.encode(adminPassword),
                roles,
                true,
                true,
                true,
                true);

        UserProfile userProfileToCreate = new UserProfile(
                null,
                adminUsername);

        UserSettings userSettings = UserSettings.withDefaults();
        userSettingsRepository.save(userSettings);

        userProfileToCreate.setUserSettings(userSettings);
        userProfileToCreate.setDecks(new ArrayList<>());
        userProfileToCreate.setAccount(accountToCreate);
        accountToCreate.setUserProfile(userProfileToCreate);
        accountRepository.save(accountToCreate);
        LOG.debug("Admin account with username='root' was created.");

    }
}
