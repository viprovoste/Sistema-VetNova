package com.vetnova.auth.dto;

import com.vetnova.auth.model.Rol;

public record RegistroRequest(
    String username,
    String password,
    String nombre,
    String apellido,
    Rol rol
) {
}