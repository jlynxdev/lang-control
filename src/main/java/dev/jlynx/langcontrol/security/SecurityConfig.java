package dev.jlynx.langcontrol.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.*;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.function.Supplier;

/**
 * Configuration class for application-level security settings, enabled by
 * Spring Security annotations {@link EnableWebSecurity} and {@link Configuration}.
 * <p>
 * Contains configuration for cross-site request forgery (CSRF) handling, request
 * authorisation, login, logout and password encoding. It also defines and manages
 * application security filters, custom request handlers and URL-based access control.
 */
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Value("${rsa.private-key}")
    private RSAPrivateKey privateKey;

    @Value("${rsa.public-key}")
    private RSAPublicKey publicKey;

    /**
     * Configures a {@link SecurityFilterChain} bean which is the central point for defining
     * the application's security rules.
     *
     * <p>
     * This method sets up the following components:
     * <ul>
     *   <li>CSRF protection using {@link CookieCsrfTokenRepository} and {@link SpaCsrfTokenRequestHandler}</li>
     *   <li>{@link CsrfCookieFilter} for advanced CSRF token handling</li>
     *   <li>Authorisation rules for specific request matchers</li>
     *   <li>Form-based login with custom success and failure handlers</li>
     *   <li>Logout behaviour configurations</li>
     * </ul>
     * </p>
     *
     * @param http the {@link HttpSecurity} object used to configure security filters and authorisation rules
     * @return a built {@link SecurityFilterChain} instance handling the application's security mechanism
     * @throws Exception if there is an error during configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrf) -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
                )
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login", "/register").permitAll()
                        .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                        .requestMatchers("/styles/**", "/scripts/**", "/*.html", "/icons/**", "/*.svg").permitAll()
                        .requestMatchers("/api/admin/**", "/admin/**").hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(new FormLoginAuthenticationSuccessHandler())
                        .failureHandler(new FormLoginAuthenticationFailureHandler())
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessUrl("/login?logout")
                );
//                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

//    /**
//     * Provides temporary in-memory users with user and admin roles for development purposes.
//     * WARNING: Should not be used in production!
//     *
//     * @return an in-memory {@code UserDetailsService} implementation
//     */
//    @Bean
//    public UserDetailsService inMemoryUsers() {
//        UserDetails user = User.builder()
//                .username("user")
//                .password("$2a$10$cByk53c5OqPtHyrzMF23FeXc7weAMcmV9OIWrFvYXWhoJGl2LmO8m") // password: user
//                .roles("USER")
//                .build();
//        UserDetails admin = User.builder()
//                .username("admin")
//                .password("$2a$10$dAaUzoiUTI3ObIL.qZeRdeWcrT01gJXsZKsB6yBfd.Y2R9tPB6fN6") // password: admin
//                .roles("ADMIN", "USER")
//                .build();
//        return new InMemoryUserDetailsManager(user, admin);
//    }

    /**
     * Provides a bean for password encoding.
     *
     * <p>
     * This method creates a {@link BCryptPasswordEncoder} instance to securely hash
     * passwords using the bcrypt hashing algorithm. The generated {@link PasswordEncoder}
     * is typically used by the authentication system to encode and verify passwords.
     * </p>
     *
     * @return a {@link PasswordEncoder} instance for password encoding operations
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    JwtDecoder jwtDecoder() {
//        return NimbusJwtDecoder.withPublicKey(this.publicKey).build();
//    }
//
//    @Bean
//    JwtEncoder jwtEncoder() {
//        JWK jwk = new RSAKey.Builder(this.publicKey).privateKey(this.privateKey).build();
//        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
//        return new NimbusJwtEncoder(jwkSource);
//    }
//
//    @Bean
//    public AuthenticationManager authManager(UserDetailsService userDetailsService) {
//        DaoAuthenticationProvider daoAuthProvider = new DaoAuthenticationProvider();
//        daoAuthProvider.setUserDetailsService(userDetailsService);
//        daoAuthProvider.setPasswordEncoder(passwordEncoder());
//        return new ProviderManager(daoAuthProvider);
//    }

    /**
     * A specialised CSRF token handler for single-page applications (SPA) that extends
     * {@link CsrfTokenRequestAttributeHandler} to customise CSRF token value resolution
     * and handling behaviour.
     *
     * <p>This handler delegates CSRF-related operations to an encapsulated instance
     * of {@link XorCsrfTokenRequestAttributeHandler}, ensuring modular and adaptable handling of
     * the CSRF token lifecycle.
     *
     * <p>Key functionalities:
     * <ul>
     * <li>Customises handling of CSRF tokens in HTTP requests and responses via {@code handle()}.</li>
     * <li>Overrides CSRF token value resolution to check for the presence of token headers, while
     * falling back to the delegate handler if the header is absent.</li>
     * </ul>
     * <p>
     * This class is used in {@link SecurityConfig} to establish SPA-specific CSRF
     * protection in the security filter chain.
     *
     * @see CsrfTokenRequestHandler
     * @see SecurityConfig
     */
    static final class SpaCsrfTokenRequestHandler extends CsrfTokenRequestAttributeHandler {

        private final CsrfTokenRequestHandler delegate = new XorCsrfTokenRequestAttributeHandler();

        /**
         * Delegates the handling of CSRF tokens to an encapsulated {@link CsrfTokenRequestHandler}.
         *
         * @param request   the {@link HttpServletRequest} representing the incoming request
         * @param response  the {@link HttpServletResponse} for sending the response
         * @param csrfToken a {@link Supplier} providing the {@link CsrfToken} to be used during handling
         */
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, Supplier<CsrfToken> csrfToken) {
            this.delegate.handle(request, response, csrfToken);
        }

        /**
         * Resolves the CSRF token value from the given {@link HttpServletRequest}.
         * This implementation first checks the request headers for the token's presence
         * and delegates to the parent implementation if the header contains the expected value.
         * If the header is absent or not valid, it falls back to the delegate handler for resolution.
         *
         * @param request   the {@link HttpServletRequest} from which the CSRF token value is resolved
         * @param csrfToken the {@link CsrfToken} containing details of the token to be resolved
         * @return the resolved CSRF token value as a {@link String}, or {@code null} if resolution fails
         */
        @Override
        public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
            if (StringUtils.hasText(request.getHeader(csrfToken.getHeaderName()))) {
                return super.resolveCsrfTokenValue(request, csrfToken);
            }
            return this.delegate.resolveCsrfTokenValue(request, csrfToken);
        }
    }

    /**
     * A filter that ensures the CSRF token is accessed during request processing.
     *
     * <p>
     * This implementation extends {@link OncePerRequestFilter} and retrieves the CSRF token
     * from the request attribute named {@code _csrf}. It ensures that the token is generated
     * by invoking {@code csrfToken.getToken()} before passing the request to the next filter
     * in the chain.
     * </p>
     *
     * @see OncePerRequestFilter
     * @see CsrfToken
     * @see SecurityConfig
     */
    static final class CsrfCookieFilter extends OncePerRequestFilter {

        /**
         * Filters the HTTP request to access the CSRF token and ensures it is invoked.
         * Passes the request and response to the next filter in the chain.
         *
         * @param request     the {@link HttpServletRequest} object representing the client's request
         * @param response    the {@link HttpServletResponse} object for sending the response back to the client
         * @param filterChain the {@link FilterChain} allowing the request to proceed through the filter chain
         * @throws ServletException if a servlet-specific error occurs
         * @throws IOException      if an I/O error occurs
         */
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
            CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
            csrfToken.getToken();
            filterChain.doFilter(request, response);
        }
    }
}
