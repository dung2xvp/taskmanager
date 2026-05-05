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
import java.time.LocalDate;

@Entity
@Table(
        name = "cards",
        indexes = {
                @Index(name = "idx_card_board_list_pos", columnList = "board_id,list_id,position"),
                @Index(name = "idx_card_due_date", columnList = "due_date"),
                @Index(name = "idx_card_created_by", columnList = "created_by")
        }
)
public class Card extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_id", nullable = false)
    public Board board;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "list_id", nullable = false)
    public BoardList list;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    public String title;

    @Size(max = 4000)
    @Column(length = 4000)
    public String description;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    public User createdBy;

    @Column(name = "start_date")
    public LocalDate startDate;

    @Column(name = "due_date")
    public LocalDate dueDate;

    @Column(nullable = false)
    public Long position = 1000L;

    @Column(name = "cover_color", length = 20)
    public String coverColor;

    @Column(nullable = false)
    public boolean archived = false;
}
