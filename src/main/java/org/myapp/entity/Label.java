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
        name = "labels",
        uniqueConstraints = @UniqueConstraint(columnNames = {"board_id", "name"}),
        indexes = {
                @Index(name = "idx_label_board", columnList = "board_id")
        }
)
public class Label extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_id", nullable = false)
    public Board board;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    public String name;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    public String color;
}
