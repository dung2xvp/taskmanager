package org.myapp.dto.user;

import org.myapp.entity.SystemRole;

public record UserResponseDto(
        Long id,
        String username,
        String email,
        String fullName,
        SystemRole systemRole,
        boolean active
) {
}
