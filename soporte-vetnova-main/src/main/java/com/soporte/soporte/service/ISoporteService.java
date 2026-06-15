package com.soporte.soporte.service;

import com.soporte.soporte.model.Soporte;

import java.util.List;
import java.util.Optional;

public interface ISoporteService {
    Soporte guardar(Soporte soporte);
    List<Soporte> listarTodos();
    Optional<Soporte> buscarPorId(Long id);
    void eliminar(Long id);
    boolean existeDuplicado(Long usuarioId, String asunto);
}