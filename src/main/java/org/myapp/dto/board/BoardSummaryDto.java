package org.myapp.dto.board;

import org.myapp.entity.BoardVisibility;

public record BoardSummaryDto(
        Long id,
        String name,
        String description,
        Long ownerId,
        BoardVisibility visibility
) {
}
