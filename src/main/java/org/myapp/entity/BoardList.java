package org.myapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(
        name = "board_lists",
        indexes = {
                @Index(name = "idx_board_list_board_pos", columnList = "board_id,position")
        }
)
public class BoardList extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_id", nullable = false)
    public Board board;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    public String name;

    @Column(nullable = false)
    public Long position = 1000L;

    @Column(nullable = false)
    public boolean archived = false;
}
