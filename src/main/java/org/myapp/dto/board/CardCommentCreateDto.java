package org.myapp.dto.board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CardCommentCreateDto {
    @NotBlank
    @Size(max = 2000)
    public String content;
}
