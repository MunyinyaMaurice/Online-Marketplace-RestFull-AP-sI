package com.Awesome.Challenge.Online.Marketplace.API.secuirity.config;

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
import static com.Awesome.Challenge.Online.Marketplace.API.model.Permission.*;
import static com.Awesome.Challenge.Online.Marketplace.API.model.Role.*;
import static org.springframework.http.HttpMethod.*;
//import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {
    private static final String[] WHITE_LIST_URL = {"/api/v2/auth/**",
            "/api/v2/products/listed",
            "/api/v2/products/{searchParam}",
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
        "/api/v2/order/submit_order",
        "/api/v2/reviews/submitReview",
        "/api/v2/users/{userId}",
        "/api/v2/products/ordered",
    };
    private static final String[] ADMIN_SELLER_LIST_URL = {
           
"/api/v2/order/received",
"/api/v2/order/{orderId}",
"/api/v2/reviews/{productId}",
"/api/v2/reviews/average-rating/{productId}",
"/api/v2/product/create_product",
"/api/v2/images/upload/{productId}",
"/api/v2/images/{imageId}",
    };
    private static final String[] ADMIN_LIST_URL = {
        "/api/v2/category/**",
        "/api/v2/product/All_products",
        "/api/v2/product/images/{productId}",
        "/api/v2/product/{productId}",
        "/api/v2/product/{productId}",
        "/api/v2/product/notListed",
        "/api/v2/product/sortedByRating",
        "/api/v2/product/highRated",
        "/api/v2/product/popular",
        "/api/v2/users/all_users",
        "/api/v2/users/{userIdToUpdate}",

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
