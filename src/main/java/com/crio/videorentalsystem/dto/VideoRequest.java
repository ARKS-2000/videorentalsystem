package com.crio.videorentalsystem.dto;

import jakarta.validation.constraints.NotBlank;

public record VideoRequest(
    @NotBlank String title,
    @NotBlank String director,
    @NotBlank String genre,
    Boolean available
) {}