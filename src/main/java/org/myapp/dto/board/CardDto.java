package org.myapp.dto.board;

import java.time.LocalDate;
import java.util.List;

public class CardDto {

    public Long id;
    public Long listId;
    public String title;
    public String description;
    public Long position;
    public LocalDate startDate;
    public LocalDate dueDate;
    public String coverColor;
    public List<Long> memberIds;
    public List<Long> labelIds;
}
