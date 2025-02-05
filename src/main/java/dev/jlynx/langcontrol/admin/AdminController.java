package dev.jlynx.langcontrol.admin;

import dev.jlynx.langcontrol.admin.dto.DeleteAccountAdminRequest;
import dev.jlynx.langcontrol.admin.dto.UpdatePasswordAdminRequest;
import dev.jlynx.langcontrol.admin.dto.UpdateUserRequest;
import dev.jlynx.langcontrol.admin.dto.UserOverview;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing administrative user operations.
 *
 * <p>This class provides endpoints for administrative functions related to user accounts,
 * including retrieving, updating, and deleting users. It uses the {@link AdminService}
 * to perform the underlying operations and ensure consistency.
 */
@Validated
@RequestMapping("${apiPref}/admin")
@RestController
public class AdminController {

    private static Logger LOG = LoggerFactory.getLogger(AdminController.class);

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Retrieves a list of all users with a simplified overview of their account information.
     *
     * @return a {@link ResponseEntity} containing a {@link List} of {@link UserOverview} objects
     * representing all users within the system, with an HTTP 200 (OK) status code.
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserOverview>> getAllUsers() {
        List<UserOverview> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Retrieves a user by their unique account ID.
     *
     * @param accountId the unique ID of the user account to retrieve. Must be a positive integer ({@code @Min(1)}).
     * @return a {@link ResponseEntity} containing the {@link UserOverview} of the specified user.
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<UserOverview> getUserById(@Min(1) @PathVariable("id") long accountId) {
        UserOverview userOverview = adminService.getUserById(accountId);
        return ResponseEntity.ok(userOverview);
    }

    /**
     * Updates the user account details for a given user ID.
     *
     * <p>Performs an update operation on the user's account based on the information
     * provided in the {@link UpdateUserRequest} object.
     *
     * @param accountId the unique ID of the user being updated; must be greater than
     *                  or equal to 1.
     * @param body      the {@link UpdateUserRequest} containing the new details for the
     *                  user; must be valid and adhere to input constraints.
     * @return a {@link ResponseEntity} with no content indicating the update was
     * successful.
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<Void> updateUser(
            @Min(1) @PathVariable("id") long accountId,
            @RequestBody @Valid UpdateUserRequest body
    ) {
        adminService.updateUser(accountId, body);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Updates the password of a specific user account.
     *
     * <p>This method allows an admin to overwrite a user's password by providing
     * the new password and the admin's password for validation.
     *
     * @param accountId the ID of the user account whose password is to be updated, must be greater than 0
     * @param body      the {@link UpdatePasswordAdminRequest} containing the admin's password and the new password for the user
     * @return a {@link ResponseEntity} with {@link HttpStatus#NO_CONTENT} if the operation is successful
     */
    @PutMapping("/users/{id}/password")
    public ResponseEntity<Void> updateUserPassword(@Min(1) @PathVariable("id") long accountId,
                                                   @Valid @RequestBody UpdatePasswordAdminRequest body) {
        LOG.debug("updateUserPassword() method invoked");
        adminService.overwriteUserPassword(accountId, body);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Deletes a user account based on the provided account ID and request body.
     *
     * <p>This operation is restricted to administrators and requires the
     * administrator's credentials for authorisation.
     *
     * @param accountId The ID of the account to be deleted. Must be a positive value.
     * @param body      The {@link DeleteAccountAdminRequest} object containing the administrator's
     *                  password for authorisation.
     * @return A {@link ResponseEntity} with a status of {@code HttpStatus.NO_CONTENT}
     * upon successful account deletion.
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUserAccount(@Min(1) @PathVariable("id") long accountId,
                                                  @Valid @RequestBody DeleteAccountAdminRequest body) {
        adminService.deleteAccount(accountId, body);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
