package com.crio.videorentalsystem.dto;

import com.crio.videorentalsystem.entity.Role;

public record AuthResponse(
    Long id, 
    String email, 
    String firstName, 
    String lastName, 
    Role role, 
    String token,
    String message
) {}