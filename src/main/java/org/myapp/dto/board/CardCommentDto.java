package org.myapp.dto.board;

import java.time.LocalDateTime;

public class CardCommentDto {
    public Long id;
    public String content;
    public Long authorId;
    public String authorName;
    public LocalDateTime createdAt;
}
