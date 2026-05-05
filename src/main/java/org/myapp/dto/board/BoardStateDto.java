package org.myapp.dto.board;

import java.util.List;

public record BoardStateDto(
        BoardSummaryDto board,
        List<BoardMemberDto> members,
        List<LabelDto> labels,
        List<BoardListDto> lists
) {
}
