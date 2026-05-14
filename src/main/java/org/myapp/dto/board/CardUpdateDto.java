package org.myapp.dto.board;

import jakarta.validation.constraints.Size;

public class CardUpdateDto {

    @Size(max = 500)
    public String title;

    @Size(max = 2000)
    public String description;

    /** Hex color, VD: "#ef4444" */
    public String coverColor;

    /** ISO date string, VD: "2025-12-31" */
    public String dueDate;

    /** ISO date string */
    public String startDate;
}
