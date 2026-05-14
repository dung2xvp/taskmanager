package org.myapp.dto.board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BoardCreateDto {

    @NotBlank
    @Size(max = 150)
    public String name;

    @Size(max = 1000)
    public String description;
}
