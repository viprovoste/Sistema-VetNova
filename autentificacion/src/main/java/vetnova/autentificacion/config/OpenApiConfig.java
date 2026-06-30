package vetnova.autentificacion.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI authOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("VetNova - Microservicio de Autenticación")
                        .description("Gestiona usuarios, roles, login/logout y validación de tokens para el "
                                + "ecosistema de microservicios de Clínica Veterinaria VetNova. Este servicio es "
                                + "consumido por Inventario y Ventas para validar sesiones y obtener datos de usuario.")
                        .version("1.0.0")
                        .contact(new Contact().name("Equipo VetNova").email("contacto@vetnova.cl"))
                        .license(new License().name("Uso académico - Duoc UC")));
    }
}
