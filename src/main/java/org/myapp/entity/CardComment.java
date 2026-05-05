package org.myapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(
        name = "card_comments",
        indexes = {
                @Index(name = "idx_card_comment_card", columnList = "card_id"),
                @Index(name = "idx_card_comment_author", columnList = "author_id")
        }
)
public class CardComment extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "card_id", nullable = false)
    public Card card;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    public User author;

    @NotBlank
    @Size(max = 2000)
    @Column(nullable = false, length = 2000)
    public String content;
}
