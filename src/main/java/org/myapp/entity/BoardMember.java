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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "board_members",
        uniqueConstraints = @UniqueConstraint(columnNames = {"board_id", "user_id"}),
        indexes = {
                @Index(name = "idx_board_member_board", columnList = "board_id"),
                @Index(name = "idx_board_member_user", columnList = "user_id")
        }
)
public class BoardMember extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_id", nullable = false)
    public Board board;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    public BoardRole role = BoardRole.MEMBER;

    @Column(name = "joined_at", nullable = false)
    public LocalDateTime joinedAt;

    @Override
    protected void onCreate() {
        super.onCreate();
        if (joinedAt == null) {
            joinedAt = createdAt;
        }
    }
}
