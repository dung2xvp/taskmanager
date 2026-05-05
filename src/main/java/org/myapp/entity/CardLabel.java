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
        name = "card_labels",
        uniqueConstraints = @UniqueConstraint(columnNames = {"card_id", "label_id"}),
        indexes = {
                @Index(name = "idx_card_label_card", columnList = "card_id"),
                @Index(name = "idx_card_label_label", columnList = "label_id")
        }
)
public class CardLabel extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "card_id", nullable = false)
    public Card card;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "label_id", nullable = false)
    public Label label;
}
