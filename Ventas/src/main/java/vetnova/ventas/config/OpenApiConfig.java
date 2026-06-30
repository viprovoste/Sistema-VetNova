package vetnova.ventas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ventasOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("VetNova - Microservicio de Ventas y Facturación")
                        .description("Gestiona el carrito de compra web, las ventas (web y en sucursal) y la "
                                + "emisión de boletas/facturas. Se comunica con Inventario para verificar stock y "
                                + "registrar salidas, y con Autenticación para validar clientes y obtener su RUT.")
                        .version("1.0.0")
                        .contact(new Contact().name("Equipo VetNova").email("contacto@vetnova.cl"))
                        .license(new License().name("Uso académico - Duoc UC")));
    }
}
