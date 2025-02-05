package dev.jlynx.langcontrol.admin;

import dev.jlynx.langcontrol.admin.dto.DeleteAccountAdminRequest;
import dev.jlynx.langcontrol.admin.dto.UpdatePasswordAdminRequest;
import dev.jlynx.langcontrol.admin.dto.UpdateUserRequest;
import dev.jlynx.langcontrol.admin.dto.UserOverview;
import java.util.List;

/**
 * Provides administrative services for managing user accounts.
 *
 * <p>The interface defines operations to:
 * <ul>
 *   <li>Retrieve an overview of all users or specific users by ID.</li>
 *   <li>Update user details such as username, first name and account states.</li>
 *   <li>Reset user passwords as an administrator.</li>
 *   <li>Delete user accounts securely after validating admin credentials.</li>
 * </ul>
 *
 * The methods are typically used in the context of application administration.
 */
public interface AdminService {

    List<UserOverview> getAllUsers();

    UserOverview getUserById(long accountId);

    void updateUser(long accountId, UpdateUserRequest body);

    void overwriteUserPassword(long accountId, UpdatePasswordAdminRequest body);

    void deleteAccount(long accountId, DeleteAccountAdminRequest body);
}
