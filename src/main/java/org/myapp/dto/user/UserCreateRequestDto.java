package org.myapp.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserCreateRequestDto {

    @NotBlank
    @Size(max = 50)
    public String username;

    @NotBlank
    @Email
    @Size(max = 100)
    public String email;

    @NotBlank
    @Size(min = 6, max = 255)
    public String password;

    @NotBlank
    @Size(max = 100)
    public String fullName;
}
