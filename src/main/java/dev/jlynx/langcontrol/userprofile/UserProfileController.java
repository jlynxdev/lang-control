package dev.jlynx.langcontrol.userprofile;

import dev.jlynx.langcontrol.userprofile.dto.UserProfileOverview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A REST controller that manages user profile-related operations.
 * <p>
 * This controller exposes endpoints for interacting with the currently authenticated user's profile.
 * It relies on {@link UserProfileService} for executing business logic.
 *
 * @see UserProfileService
 * @see UserProfile
 */
@RequestMapping("${apiPref}/profile")
@RestController
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Autowired
    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    /**
     * Retrieves the profile overview of the currently authenticated user.
     *
     * @return a {@link ResponseEntity} containing a {@link UserProfileOverview} object
     * representing the currently authenticated user's profile.
     */
    @GetMapping
    public ResponseEntity<UserProfileOverview> getCurrentUserProfile() {
        UserProfileOverview overview = userProfileService.getCurrentUserProfile();
        return ResponseEntity.ok(overview);
    }
}
