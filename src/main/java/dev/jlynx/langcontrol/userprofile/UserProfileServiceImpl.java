package dev.jlynx.langcontrol.userprofile;

import dev.jlynx.langcontrol.account.Account;
import dev.jlynx.langcontrol.account.dto.UpdateAccountAndUserProfileRequest;
import dev.jlynx.langcontrol.exception.ValuesTheSameException;
import dev.jlynx.langcontrol.userprofile.dto.UserProfileOverview;
import dev.jlynx.langcontrol.util.AuthRetriever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final AuthRetriever authRetriever;

    @Autowired
    public UserProfileServiceImpl(UserProfileRepository userProfileRepository,
                                  AuthRetriever authRetriever) {
        this.userProfileRepository = userProfileRepository;
        this.authRetriever = authRetriever;
    }

    /**
     * Retrieves the {@link UserProfile} linked to the currently authenticated account.
     *
     * @return the {@link UserProfile} entity associated with the currently authenticated {@link Account}
     */
    @Transactional
    @Override
    public UserProfile retrieveCurrentProfileEntity() {
        return authRetriever.retrieveCurrentAccount().getUserProfile();
    }

    /**
     * Retrieves an overview of the currently authenticated user's {@link UserProfile}.
     *
     * @return an instance of {@link UserProfileOverview} constructed from the
     * {@link UserProfile} associated with the currently authenticated {@link Account}.
     */
    @Override
    public UserProfileOverview getCurrentUserProfile() {
        return UserProfileOverview.fromEntity(authRetriever.retrieveCurrentAccount().getUserProfile());
    }

    @Transactional
    @Override
    public void updateCurrentProfile(UpdateAccountAndUserProfileRequest body) {
        UserProfile currentProfile = this.retrieveCurrentProfileEntity();
        currentProfile.setFirstName(body.firstName());
    }
}
