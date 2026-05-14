package org.myapp.dto.board;

import java.util.List;

public class BoardStateDto {

    public BoardSummaryDto board;
    public List<BoardMemberDto> members;
    public List<LabelDto> labels;
    public List<BoardListDto> lists;
}
