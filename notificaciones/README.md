# Microservicio de Notificaciones - VetNova

## Descripción
Microservicio encargado de gestionar las notificaciones del sistema VetNova. Permite crear, listar, actualizar y eliminar notificaciones, con validación de duplicados por cita, tipo y estado.

## Integrantes
- Sebastián Miranda
- Vicente Provoste
- Bastián Chamblas

## Tecnologías
- Java 17
- Spring Boot 3.2.5
- MySQL
- JUnit 5 + Mockito
- Swagger / OpenAPI (springdoc-openapi)

## Endpoints principales
| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | /api/notificaciones | Crear notificación |
| GET | /api/notificaciones | Listar todas |
| GET | /api/notificaciones/{id} | Obtener por ID |
| PUT | /api/notificaciones/{id} | Actualizar |
| DELETE | /api/notificaciones/{id} | Eliminar |

## Documentación Swagger
- Local: http://localhost:8088/swagger-ui/index.html
- JSON: http://localhost:8088/v3/api-docs

## Ejecución local
1. Tener XAMPP corriendo con MySQL activo
2. Asegurarse de que la base de datos `notificaciones_db` existe
3. Ejecutar:
```bash
./mvnw spring-boot:run
```
4. El servicio estará disponible en el puerto **8088**

## Pruebas unitarias
```bash
./mvnw test
```