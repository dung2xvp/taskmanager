package org.myapp.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserUpdateDto {
    @NotBlank
    @Size(max = 100)
    public String fullName;
}
