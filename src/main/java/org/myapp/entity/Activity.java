package org.myapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(
        name = "activities",
        indexes = {
                @Index(name = "idx_activity_board_created", columnList = "board_id,created_at"),
                @Index(name = "idx_activity_card_created", columnList = "card_id,created_at"),
                @Index(name = "idx_activity_actor_created", columnList = "actor_id,created_at")
        }
)
public class Activity extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_id", nullable = false)
    public Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id")
    public Card card;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "actor_id", nullable = false)
    public User actor;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    public ActivityType type;

    @Size(max = 4000)
    @Column(length = 4000)
    public String payload;
}
