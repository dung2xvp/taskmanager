package org.myapp.dto.auth;

import java.time.LocalDateTime;

public class AdminUserDto {
    public Long id;
    public String username;
    public String email;
    public String fullName;
    public boolean active;
    public String systemRole;
    public LocalDateTime createdAt;
}
