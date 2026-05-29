package com.vetnova.auth.dto;

public record LoginRequest(
    String username,
    String password
) {
}
