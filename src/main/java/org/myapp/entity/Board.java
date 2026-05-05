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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(
        name = "boards",
        indexes = {
                @Index(name = "idx_board_owner", columnList = "owner_id"),
                @Index(name = "idx_board_visibility", columnList = "visibility")
        }
)
public class Board extends BaseEntity {

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    public String name;

    @Size(max = 1000)
    @Column(length = 1000)
    public String description;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    public User owner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    public BoardVisibility visibility = BoardVisibility.PRIVATE;

    @Column(nullable = false)
    public boolean archived = false;
}
