package dev.jlynx.langcontrol.account;

import dev.jlynx.langcontrol.account.dto.AccountOverviewResponse;
import dev.jlynx.langcontrol.account.dto.DeleteAccountRequest;
import dev.jlynx.langcontrol.account.dto.UpdateAccountAndUserProfileRequest;
import dev.jlynx.langcontrol.account.dto.UpdatePasswordRequest;
import dev.jlynx.langcontrol.exception.ErrorResponseBody;
import dev.jlynx.langcontrol.exception.ValuesTheSameException;
import dev.jlynx.langcontrol.exception.WrongPasswordException;
import dev.jlynx.langcontrol.userprofile.UserProfileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * The {@code AccountController} class defines endpoints related to account management.
 * <p>
 * This class is a REST controller and handles HTTP requests for the {@code /api/account} path,
 * enabling functionalities including retrieving account details, updating account and user profile information,
 * changing passwords and deleting accounts.
 * It uses the {@link AccountService} and {@link UserProfileService} to delegate business logic operations.
 *
 * <ul>
 *   <li>{@code GET /api/account}: Retrieves the current account details.</li>
 *   <li>{@code PUT /api/account}: Updates the username of the current account and the user's profile details.</li>
 *   <li>{@code PUT /api/account/password}: Changes the password of the current account.</li>
 *   <li>{@code DELETE /api/account}: Deletes the current account.</li>
 * </ul>
 *
 * <p>Exception handling is provided for:
 * <ul>
 *   <li>{@link ValuesTheSameException}: Triggers a {@link HttpStatus#BAD_REQUEST} when no field changes.</li>
 *   <li>{@link WrongPasswordException}: Triggers a {@link HttpStatus#FORBIDDEN} for incorrect password entries.</li>
 * </ul>
 */
@RequestMapping("${apiPref}/account")
@RestController
public class AccountController {

    private final AccountService accountService;
    private final UserProfileService userProfileService;

    @Autowired
    public AccountController(AccountService accountService, UserProfileService userProfileService) {
        this.accountService = accountService;
        this.userProfileService = userProfileService;
    }

    /**
     * Retrieves the current account overview for the authenticated user.
     * <p>
     * This method uses the {@link AccountService#getCurrentAccount()} to fetch the account details
     * and returns them wrapped in a {@link ResponseEntity}.
     *
     * @return a {@link ResponseEntity} containing the {@link AccountOverviewResponse} of the current account.
     */
    @GetMapping
    public ResponseEntity<AccountOverviewResponse> getCurrentAccount() {
        AccountOverviewResponse overview = accountService.getCurrentAccount();
        return ResponseEntity.ok(overview);
    }

    /**
     * Updates the current account's details and user profile information.
     *
     * <p>The method delegates the update of account details to {@link AccountService#updateAccount(UpdateAccountAndUserProfileRequest)}
     * and user profile details to {@link UserProfileService#updateCurrentProfile(UpdateAccountAndUserProfileRequest)}.
     *
     * @param body a {@link UpdateAccountAndUserProfileRequest} object containing the new account and profile details.
     * @return a {@link ResponseEntity} containing an {@link AccountOverviewResponse} with updated account information.
     */
    @PutMapping
    public ResponseEntity<AccountOverviewResponse> updateAccountAndUserProfile(@Valid @RequestBody UpdateAccountAndUserProfileRequest body) {
        var updatedAccountOverview = accountService.updateAccount(body);
        userProfileService.updateCurrentProfile(body);
        return ResponseEntity.ok(updatedAccountOverview);
    }

    /**
     * Updates the password of the currently authenticated account.
     * <p>
     * This method validates the given request body and delegates the password change operation to
     * the {@link AccountService}.
     *
     * @param body the {@link UpdatePasswordRequest} containing the current and new password values
     * @return a {@link ResponseEntity} with HTTP status {@code 204 No Content} upon successful update
     * @throws WrongPasswordException if the current password provided is incorrect
     */
    @PutMapping("/password")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody UpdatePasswordRequest body) {
        var updatedAccountOverview = accountService.changePassword(body);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Deletes the current user's account.
     * <p>
     * This method removes the account along with its related entities from the system.
     * It validates the provided password before performing the operation.
     *
     * @param body the {@link DeleteAccountRequest} containing the password required for account deletion
     * @return a {@link ResponseEntity} with an HTTP status code of {@link HttpStatus#NO_CONTENT} if the account
     * is successfully deleted
     * @throws WrongPasswordException if the provided password is incorrect
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteAccount(@Valid @RequestBody DeleteAccountRequest body) {
        accountService.deleteAccount(body);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    /**
     * Handles {@link ValuesTheSameException} by returning an {@link ErrorResponseBody} wit
     * {@link HttpStatus#BAD_REQUEST}
     * <p>
     * This exception is triggered when an update operation on an account or profile does not result in any changes
     * (i.e. the values remain the same).
     *
     * @param e   the {@link ValuesTheSameException} that was thrown
     * @param req the {@link HttpServletRequest} object containing information about the HTTP request
     * @return an {@link ErrorResponseBody} containing the details of the error, including the HTTP status code
     * {@link HttpStatus#BAD_REQUEST}, request URI and exception message
     */
    @ExceptionHandler(ValuesTheSameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseBody handleValuesTheSameException(ValuesTheSameException e, HttpServletRequest req) {
        return new ErrorResponseBody(HttpStatus.BAD_REQUEST, req.getRequestURI(), e);
    }

    /**
     * Handles {@link WrongPasswordException} by returning an {@link ErrorResponseBody} with
     * {@link HttpStatus#FORBIDDEN}.
     *
     * @param e   the {@link WrongPasswordException} thrown due to incorrect password entry.
     * @param req the {@code HttpServletRequest} object containing request details.
     * @return an {@link ErrorResponseBody} containing the HTTP status, request URI and exception details.
     */
    @ExceptionHandler(WrongPasswordException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseBody handlePasswordMismatchException(WrongPasswordException e, HttpServletRequest req) {
        return new ErrorResponseBody(HttpStatus.FORBIDDEN, req.getRequestURI(), e);
    }
}
