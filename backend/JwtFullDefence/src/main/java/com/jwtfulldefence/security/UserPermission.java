package com.jwtfulldefence.security;

public enum UserPermission {

    USER_READ("user:read"),
    USER_WRITE("user:write"),
    USER_UPDATE_ONLY_PERSONAL_INFO("user:updatepi"),
    USER_UPDATE_EVERYWHERE("user:update"),
    USER_DELETE("user:delete");

    private final String permission;

    UserPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
