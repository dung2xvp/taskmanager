package org.myapp.dto.user;

import org.myapp.entity.SystemRole;

public class UserResponseDto {

    public Long id;
    public String username;
    public String email;
    public String fullName;
    public SystemRole systemRole;
    public boolean active;
}
