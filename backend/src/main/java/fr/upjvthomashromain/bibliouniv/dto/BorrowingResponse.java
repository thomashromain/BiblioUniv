package fr.upjvthomashromain.bibliouniv.dto;

import java.time.LocalDateTime;

public record BorrowingResponse(
    Long id,
    String bookTitle,
    String author,
    LocalDateTime borrowedAt,
    LocalDateTime returnDeadline,
    boolean returned
) {}
