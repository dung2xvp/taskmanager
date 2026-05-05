package org.myapp.dto.board;

import org.myapp.entity.BoardRole;

public record BoardMemberDto(
        Long userId,
        String username,
        String fullName,
        BoardRole role
) {
}
