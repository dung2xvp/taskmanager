package org.myapp.dto.board;

import jakarta.validation.constraints.NotNull;

public class CardMoveDto {

    /** ID của cột muốn chuyển tới */
    @NotNull
    public Long listId;

    /** Vị trí trong cột muốn chuyển tới */
    @NotNull
    public Integer position;
}
