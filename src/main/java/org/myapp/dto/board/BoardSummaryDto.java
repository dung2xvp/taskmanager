package org.myapp.dto.board;

import org.myapp.entity.BoardVisibility;

public class BoardSummaryDto {

    public Long id;
    public String name;
    public String description;
    public Long ownerId;
    public BoardVisibility visibility;
}
