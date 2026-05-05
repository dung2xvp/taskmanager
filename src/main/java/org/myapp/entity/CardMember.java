package org.myapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(
        name = "card_members",
        uniqueConstraints = @UniqueConstraint(columnNames = {"card_id", "user_id"}),
        indexes = {
                @Index(name = "idx_card_member_card", columnList = "card_id"),
                @Index(name = "idx_card_member_user", columnList = "user_id")
        }
)
public class CardMember extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "card_id", nullable = false)
    public Card card;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    public User user;
}
