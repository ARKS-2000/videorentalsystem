package com.crio.videorentalsystem.dto;

import java.time.LocalDateTime;

public record RentalResponse(
    Long rentalId,
    Long videoId,
    String videoTitle,
    String userEmail,
    LocalDateTime rentedAt,
    LocalDateTime returnedAt,
    boolean active,
    String message
) {}