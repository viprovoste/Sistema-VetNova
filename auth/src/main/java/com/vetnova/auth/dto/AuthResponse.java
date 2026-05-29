package com.vetnova.auth.dto;

public record AuthResponse(
    String token,
    String username,
    String rol
) {}