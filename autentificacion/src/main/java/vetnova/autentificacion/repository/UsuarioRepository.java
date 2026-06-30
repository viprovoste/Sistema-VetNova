package vetnova.autentificacion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vetnova.autentificacion.model.Rol;
import vetnova.autentificacion.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Usuario> findByRol(Rol rol);

    List<Usuario> findByActivoTrue();
}
