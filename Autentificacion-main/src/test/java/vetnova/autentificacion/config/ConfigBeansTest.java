package vetnova.autentificacion.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Beans de configuración - pruebas unitarias")
class ConfigBeansTest {

    @Test
    @DisplayName("SecurityConfig expone un PasswordEncoder que codifica y valida contraseñas")
    void creaPasswordEncoderFuncional() {
        PasswordEncoder encoder = new SecurityConfig().passwordEncoder();

        String hash = encoder.encode("clave123");

        assertThat(hash).isNotEqualTo("clave123");
        assertThat(encoder.matches("clave123", hash)).isTrue();
        assertThat(encoder.matches("otraClave", hash)).isFalse();
    }

    @Test
    @DisplayName("OpenApiConfig construye la metadata de OpenAPI con título y versión")
    void creaOpenApiInfo() {
        OpenAPI openAPI = new OpenApiConfig().authOpenAPI();
        assertThat(openAPI.getInfo().getTitle()).contains("Autenticación");
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("1.0.0");
    }
}
