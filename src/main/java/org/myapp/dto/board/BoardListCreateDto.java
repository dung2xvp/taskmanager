package org.myapp.dto.board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BoardListCreateDto {
    @NotBlank
    @Size(max = 100)
    public String name;
}
