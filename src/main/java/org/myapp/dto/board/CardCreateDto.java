package org.myapp.dto.board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CardCreateDto {

    @NotBlank
    @Size(max = 500)
    public String title;

    @Size(max = 2000)
    public String description;
}
