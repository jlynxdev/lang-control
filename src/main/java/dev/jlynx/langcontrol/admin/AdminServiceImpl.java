package dev.jlynx.langcontrol.admin;

import dev.jlynx.langcontrol.account.Account;
import dev.jlynx.langcontrol.account.AccountRepository;
import dev.jlynx.langcontrol.account.AccountService;
import dev.jlynx.langcontrol.admin.dto.DeleteAccountAdminRequest;
import dev.jlynx.langcontrol.admin.dto.UpdatePasswordAdminRequest;
import dev.jlynx.langcontrol.admin.dto.UpdateUserRequest;
import dev.jlynx.langcontrol.admin.dto.UserOverview;
import dev.jlynx.langcontrol.exception.AssetNotFoundException;
import dev.jlynx.langcontrol.exception.WrongPasswordException;
import dev.jlynx.langcontrol.userprofile.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private static final Logger LOG = LoggerFactory.getLogger(AdminServiceImpl.class);

    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final PasswordEncoder encoder;

    @Autowired
    public AdminServiceImpl(AccountRepository accountRepository, AccountService accountService, PasswordEncoder encoder) {
        this.accountRepository = accountRepository;
        this.accountService = accountService;
        this.encoder = encoder;
    }

    /**
     * Retrieves a list of all user accounts and their corresponding simplified overviews.
     *
     * <p>This method fetches all {@link Account} entities from the data source, extracts
     * their associated {@link UserProfile}s, and maps them to {@link UserOverview} objects
     * which provide a concise view of the user's data.
     *
     * @return a {@link List} of {@link UserOverview} objects, each representing a user's key details.
     */
    @Override
    public List<UserOverview> getAllUsers() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream()
                .map(account -> {
                    UserProfile profile = account.getUserProfile();
                    return new UserOverview(account, profile);
                })
                .toList();
    }

    /**
     * Retrieves a user's overview by their account ID.
     *
     * <p>This method fetches the {@link Account} associated with the given ID,
     * retrieves the linked {@link UserProfile}, and combines them into a {@link UserOverview}.
     *
     * @param accountId the unique identifier of the account to retrieve.
     * @return a {@link UserOverview} object containing concise details of the user.
     * @throws AssetNotFoundException if no account with the specified ID is found.
     */
    @Override
    public UserOverview getUserById(long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(AssetNotFoundException::new);
        UserProfile profile = account.getUserProfile();
        return new UserOverview(account, profile);
    }

    /**
     * Updates the user's account and profile details based on the given request.
     *
     * <ul>
     * <li>The account's username, enabled status and locked status are updated.</li>
     * <li>The user profile's first name is updated.</li>
     * </ul>
     *
     * @param accountId the unique identifier of the account to be updated
     * @param body the {@link UpdateUserRequest} containing the new account and profile details
     * @throws AssetNotFoundException if the account with the specified {@code accountId} is not found
     */
    @Transactional
    @Override
    public void updateUser(long accountId, UpdateUserRequest body) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(AssetNotFoundException::new);
        UserProfile profile = account.getUserProfile();
        account.setUsername(body.username());
        profile.setFirstName(body.firstName());
        account.setEnabled(body.enabled());
        account.setAccountNonLocked(body.nonLocked());
    }

    /**
     * Updates the password of a specified user account after validating the administrator's password.
     *
     * <p>The method verifies the provided admin password against the current authenticated
     * administrator's password. If the validation fails, a {@link WrongPasswordException} is thrown.
     * If the target account is not found, an {@link AssetNotFoundException} is thrown.
     * Upon successful validation and account retrieval, the new password is encoded and updated.
     *
     * @param accountId the ID of the user account whose password is to be updated
     * @param body an instance of {@link UpdatePasswordAdminRequest} containing the administrator's
     *             password and the new password for the target account
     * @throws WrongPasswordException if the provided admin password is incorrect
     * @throws AssetNotFoundException if no account is found with the specified ID
     */
    @Transactional
    @Override
    public void overwriteUserPassword(long accountId, UpdatePasswordAdminRequest body) {
        LOG.debug("overwriteUserPassword() method invoked.");
        Account currentAccount = accountService.retrieveCurrentAccountEntity();
        if (!encoder.matches(body.adminPassword(), currentAccount.getPassword())) {
            throw new WrongPasswordException("The value for the current password is incorrect.");
        }
        Account accountToUpdate = accountRepository.findById(accountId).orElseThrow(AssetNotFoundException::new);
        accountToUpdate.setPassword(encoder.encode(body.newPassword()));
        accountRepository.save(accountToUpdate);
    }

    /**
     * Deletes an account with the specified ID after verifying the admin password.
     *
     * <p>This method validates the admin password provided in the request. If the password
     * is correct, the account with the given ID is retrieved from the repository and deleted.
     *
     * @param accountId the ID of the account to be deleted
     * @param body the {@link DeleteAccountAdminRequest} containing the admin password for validation
     * @throws WrongPasswordException if the provided admin password does not match the current password
     * @throws AssetNotFoundException if the account with the specified ID is not found
     */
    @Transactional
    @Override
    public void deleteAccount(long accountId, DeleteAccountAdminRequest body) {
        Account currentAccount = accountService.retrieveCurrentAccountEntity();
        if (!encoder.matches(body.adminPassword(), currentAccount.getPassword())) {
            throw new WrongPasswordException("The value for the current password is incorrect.");
        }
        Account accountToDelete = accountRepository.findById(accountId)
                .orElseThrow(() -> new AssetNotFoundException(
                        "Account with id=" + accountId + " couldn't be found."));
        accountRepository.deleteById(accountId);
    }
}
