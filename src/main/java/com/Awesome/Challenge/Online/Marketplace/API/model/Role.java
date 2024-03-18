package com.Awesome.Challenge.Online.Marketplace.API.model;

import io.jsonwebtoken.lang.Collections;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.Awesome.Challenge.Online.Marketplace.API.model.Permission.*;

@RequiredArgsConstructor
public enum Role {
//    BUYER(Collections.emptySet()),
    BUYER(
            Set.of(
                    BUYER_UPDATE,
                    BUYER_READ,
                    BUYER_ORDER
            )
    ),
    ADMIN(
            Set.of(
                    ADMIN_READ,
                    ADMIN_UPDATE,
                    ADMIN_DELETE,
                    ADMIN_CREATE,
                    SELLER_READ,
                    SELLER_UPDATE,
                    SELLER_DELETE,
                    SELLER_CREATE
            )
    ),
    SELLER(
            Set.of(
                    SELLER_READ,
                    SELLER_UPDATE,
                    SELLER_DELETE,
                    SELLER_CREATE
            )
    )

    ;
    public static boolean hasRole(Set<Role> roles, Role role) {
        return roles.contains(role);
    }

    @Getter
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
