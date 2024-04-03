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
    private static final String[] WHITE_LIST_URL = {"/api/v1/auth/**",
            "/api/l1/search/{searchParam}",
            "/api/l1/listed",
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
            "/api/l2/order/press_order",
            "/api/l2/reviews/submitReview",
            "/api/l2/users/update/{userId}",
            "/api/l1/buyer"
    };
    private static final String[] ADMIN_SELLER_LIST_URL = {
            "/api/l2/order/orders_list",
            "/api/l2/order/update/{orderId}",
            "/api/l2/reviews/product/{productId}",
            "/api/l2/reviews/product/{productId}/average-rating",
            "/api/l3/product/create_product",
            "/api/l3/images/products/{productId}"
    };
    private static final String[] ADMIN_LIST_URL = {
            "/api/l3/category/createCategory",
            "/api/l3/product/All_products",
            "/api/l3/product/image/{productId}",
            "/api/l3/product/update/{productId}",
            "/api/l3/product/del/{productId}",
            "/api/l3/product/notListed",
            "/api/l3/product/sortedByRating",
            "/api/l3/product/highRated",
            "/api/l3/product/popular",
            "/api/l3/images/upload/{productId}",
            "/api/l3/images/del/{imageId}",
            "/api/l3/category/all_list",
            "/api/l3/category/update/{categoryId}",
            "/api/l3/category/del/{categoryId}",
            "/api/l2/users/all_users",
            "/api/l2/users/updateRole/{userIdToUpdate}"

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
                .logout(logout -> logout.logoutUrl("/api/v1/auth/logout")
                .addLogoutHandler(logoutHandler)
                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                );
        return http.build();
    }
}
