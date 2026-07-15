package vetnova.autentificacion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vetnova.autentificacion.model.Credencial;

import java.util.Optional;

public interface CredencialRepository extends JpaRepository<Credencial, Long> {

    Optional<Credencial> findByTokenAndActivaTrue(String token);

    Optional<Credencial> findByToken(String token);
}
