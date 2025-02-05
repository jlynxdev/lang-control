package dev.jlynx.langcontrol;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configures various web-related settings for the application.
 *
 * <p>This class enables Spring's Data web support, specifically using
 * {@code PageSerializationMode.VIA_DTO}, and defines custom MVC configurations.
 *
 * <p>It provides mappings between specific URL endpoints and their
 * corresponding static HTML views, ensuring a separation between
 * backend services and frontend files.
 *
 * <ul>
 *   <li>Uses {@link Configuration} to designate this class as a Spring configuration class.</li>
 *   <li>Applies {@link EnableSpringDataWebSupport} for enhanced Spring Data web integration.</li>
 * </ul>
 */
@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class WebConfig {

//    @Bean
//    public InternalResourceViewResolver internalResourceViewResolver() {
//        InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
//        internalResourceViewResolver.setPrefix("static/");
//        internalResourceViewResolver.setSuffix(".html");
//        return internalResourceViewResolver;
//    }

    /**
     * Configures Web MVC for the application.
     *
     * <p>Provides custom view controllers that map specific URL paths
     * to corresponding HTML files using the {@code forward} directive.
     *
     * @return a {@link WebMvcConfigurer} that defines view controller mappings
     * for various endpoints like registration, login and administrative pages.
     */
    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                registry.addViewController("/register").setViewName("forward:/register.html");
                registry.addViewController("/login").setViewName("forward:/login.html");
                registry.addViewController("/create/card").setViewName("forward:/create-card.html");
                registry.addViewController("/create/deck").setViewName("forward:/create-deck.html");
                registry.addViewController("/browse").setViewName("forward:/browse.html");
                registry.addViewController("/review").setViewName("forward:/review.html");
                registry.addViewController("/decks/*/edit").setViewName("forward:/edit-deck.html");
                registry.addViewController("/cards/*/edit").setViewName("forward:/edit-card.html");
                registry.addViewController("/settings/app").setViewName("forward:/settings-app.html");
                registry.addViewController("/settings/account").setViewName("forward:/settings-account.html");
                registry.addViewController("/admin").setViewName("forward:/admin-users.html");
                registry.addViewController("/admin/users/*/edit").setViewName("forward:/admin-user-edit.html");
            }
        };
    }
}
