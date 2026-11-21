package com.crio.videorentalsystem.dto;

import com.crio.videorentalsystem.entity.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
    @NotBlank @Email String email,
    @NotBlank String password,
    @NotBlank String firstName,
    @NotBlank String lastName,
    Role role
) {
    public RegisterRequest {
        if (role == null) {
            role = Role.CUSTOMER;
        }
    }
}