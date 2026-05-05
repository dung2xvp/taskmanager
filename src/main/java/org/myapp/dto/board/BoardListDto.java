package org.myapp.dto.board;

import java.util.List;

public record BoardListDto(
        Long id,
        String name,
        Long position,
        List<CardDto> cards
) {
}
