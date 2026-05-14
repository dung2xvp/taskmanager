package org.myapp.dto.user;

import org.myapp.entity.SystemRole;

public class AuthResponseDto {

    public String token;
    public Long userId;
    public String username;
    public String fullName;
    public SystemRole systemRole;

    public AuthResponseDto() {}

    public AuthResponseDto(String token, Long userId, String username,
                           String fullName, SystemRole systemRole) {
        this.token      = token;
        this.userId     = userId;
        this.username   = username;
        this.fullName   = fullName;
        this.systemRole = systemRole;
    }
}
