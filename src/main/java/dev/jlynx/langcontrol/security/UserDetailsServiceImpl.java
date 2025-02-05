package dev.jlynx.langcontrol.security;

import dev.jlynx.langcontrol.account.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link UserDetailsService} that loads user-specific data.
 * This service retrieves user details from the {@link AccountRepository}.
 *
 * <p>Used for authenticating users within the Spring Security framework by their username.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Autowired
    public UserDetailsServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Loads a user's details based on their username.
     *
     * @param username the username of the user to load; must not be null.
     * @return a {@link UserDetails} object containing the user's details.
     * @throws UsernameNotFoundException if no user with the given username is found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Could not find the account with the specified username."));
    }
}
