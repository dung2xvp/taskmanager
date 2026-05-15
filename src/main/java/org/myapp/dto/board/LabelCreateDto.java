package org.myapp.dto.board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LabelCreateDto {
    @NotBlank
    @Size(max = 50)
    public String name;

    @NotBlank
    @Size(max = 20)
    public String color;
}
