package com.Awesome.Challenge.Online.Marketplace.API.security.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

//import static com.Awesome.Challenge.Online.Marketplace.API.model.Permission.ADMIN_DELETE;
import static com.Awesome.Challenge.Online.Marketplace.API.model.Role.*;
//import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {
    private static final String[] WHITE_LIST_URL = {"/api/auth/**",
            "/api/products/listed",
            "/api/products/{searchParam}",
            //LIST OF AUTHORIZED SWAGGER URLs
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html"
            };
    private static final String[] LIST_FOR_LOGGED_IN_URL = {
        "/api/order/submit_order",
        "/api/reviews/submitReview",
        "/api/users/{userId}",
        "/api/products/ordered",
    };
    private static final String[] ADMIN_SELLER_LIST_URL = {
           
"/api/order/received",
"/api/order/{orderId}",
"/api/reviews/{productId}",
"/api/reviews/average-rating/{productId}",
"/api/product/create_product",
"/api/images/upload/{productId}",
"/api/images/{imageId}",
    };
    private static final String[] ADMIN_LIST_URL = {
        "/api/category/**",
        "/api/product/All_products",
        "/api/product/images/{productId}",
        "/api/product/{productId}",
        "/api/product/{productId}",
        "/api/product/notListed",
        "/api/product/sortedByRating",
        "/api/product/highRated",
        "/api/product/popular",
        "/api/users/all_users",
        "/api/users/{userIdToUpdateRole}",

    };
//    private static final String[]
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            // Customize the error response
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Access Denied: " + accessDeniedException.getMessage());
        };
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AccessDeniedHandler accessDeniedHandler) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests(req -> req
                        .requestMatchers(WHITE_LIST_URL)
//                .requestMatchers("/api/v1/auth/**")
                        .permitAll()
                        .requestMatchers(LIST_FOR_LOGGED_IN_URL).hasAnyRole(ADMIN.name(), SELLER.name(), BUYER.name())
                        .requestMatchers(ADMIN_SELLER_LIST_URL).hasAnyRole(ADMIN.name(), SELLER.name())
                        .requestMatchers(ADMIN_LIST_URL).hasRole(ADMIN.name())
                        .anyRequest()
                        .authenticated())
                .exceptionHandling(exceptions -> exceptions
                .accessDeniedHandler(accessDeniedHandler)
        )

                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))

                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout.logoutUrl("/api/v2/auth/logout")
                .addLogoutHandler(logoutHandler)
                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                );
        return http.build();
    }
}
