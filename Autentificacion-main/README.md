# Microservicio de Autenticación - VetNova

## Descripción
Microservicio encargado de gestionar la autenticación y los usuarios del sistema VetNova. Permite registrar usuarios con distintos roles, iniciar y cerrar sesión mediante tokens, validar credenciales activas y consultar información de un usuario a partir de su token. Es el servicio central de seguridad consumido por los demás microservicios de la plataforma.

## Integrantes
- Sebastián Miranda
- Vicente Provoste
- Bastián Chamblas

## Tecnologías
- Java 25
- Spring Boot 4.1.0
- MySQL
- Spring Security Crypto (encriptación de contraseñas con BCrypt)
- JUnit 5 + Mockito + AssertJ
- JaCoCo (cobertura mínima 100%)
- Swagger / OpenAPI (springdoc-openapi 3.0.3)

## Endpoints principales

### Autenticación (`/api/auth`)
| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | /api/auth/login | Iniciar sesión y obtener token |
| POST | /api/auth/logout | Invalidar token activo |
| GET | /api/auth/validar | Verificar si un token es válido |
| GET | /api/auth/usuario-por-token | Obtener datos del usuario autenticado |

### Usuarios (`/api/usuarios`)
| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | /api/usuarios | Crear nuevo usuario |
| GET | /api/usuarios | Listar todos los usuarios |
| GET | /api/usuarios/{id} | Obtener usuario por ID |
| GET | /api/usuarios/rol/{rol} | Listar usuarios por rol |
| PUT | /api/usuarios/{id} | Actualizar datos del usuario |
| PATCH | /api/usuarios/{id}/desactivar | Desactivar usuario (soft delete) |
| PATCH | /api/usuarios/{id}/activar | Reactivar usuario |
| PUT | /api/usuarios/{id}/rol | Cambiar rol del usuario |
| PUT | /api/usuarios/{id}/password | Cambiar contraseña |

## Roles disponibles
| Rol | Descripción |
|-----|-------------|
| `ADMIN` | Acceso total al sistema |
| `ADMIN_SUCURSAL` | Administrador de una sucursal específica |
| `VETERINARIO` | Personal médico |
| `RECEPCIONISTA` | Atención de clientes y caja |
| `BODEGUERO` | Gestión de inventario y stock |

## Comunicación con otros microservicios
Este microservicio **no consume** otros servicios. Es el proveedor de identidad: Inventario y Ventas lo consultan para validar tokens y obtener datos de usuarios.

## Documentación Swagger
- Local: http://localhost:8081/swagger-ui/index.html
- JSON: http://localhost:8081/v3/api-docs

## Base de datos
- Nombre: `vetnova_auth`
- Motor: MySQL
- Tablas principales: `usuarios`, `credenciales`

## Ejecución local
1. Tener XAMPP corriendo con MySQL activo
2. Asegurarse de que la base de datos `vetnova_auth` existe (o activar `createDatabaseIfNotExist=true` en el properties)
3. Ajustar usuario y contraseña en `application.properties`
4. Ejecutar:
```bash
./mvnw spring-boot:run
```
5. El servicio estará disponible en el puerto **8081**

## Pruebas unitarias
```bash
./mvnw test
```

## Cobertura de código
```bash
./mvnw verify
```
El reporte HTML se genera en `target/site/jacoco/index.html`. El build falla si la cobertura de líneas cae por debajo del **100%** en las clases de servicio, controladores y manejo de excepciones.
