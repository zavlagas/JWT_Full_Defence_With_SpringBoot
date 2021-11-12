package com.jwtfulldefence.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static com.jwtfulldefence.security.UserPermission.*;

public enum UserRole {


    ROLE_USER(Set.of(USER_READ, USER_UPDATE_ONLY_PERSONAL_INFO)),
    ROLE_ADMIN(Set.of(USER_READ, USER_WRITE, USER_UPDATE_EVERYWHERE, USER_DELETE));

    private final Set<UserPermission> permissions;

    UserRole(Set<UserPermission> permissions) {
        this.permissions = permissions;
    }

    public Set<UserPermission> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getGrantedAuthorities() {
        Set<SimpleGrantedAuthority> permissions
                =
                getPermissions()
                        .stream()
                        .map(permission -> new SimpleGrantedAuthority(permission.getPermission())
                        ).collect(Collectors.toSet());

        return (permissions);
    }

}
