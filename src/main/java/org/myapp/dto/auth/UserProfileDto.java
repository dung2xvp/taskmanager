package org.myapp.dto.auth;

import java.time.LocalDateTime;

public class UserProfileDto {
    public Long id;
    public String username;
    public String email;
    public String fullName;
    public LocalDateTime createdAt;
}
