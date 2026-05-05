package org.myapp.dto.board;

import java.time.LocalDate;
import java.util.List;

public record CardDto(
        Long id,
        Long listId,
        String title,
        String description,
        Long position,
        LocalDate startDate,
        LocalDate dueDate,
        String coverColor,
        List<Long> memberIds,
        List<Long> labelIds
) {
}
