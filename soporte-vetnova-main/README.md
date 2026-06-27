# Microservicio de Soporte - VetNova

## Descripción
Microservicio encargado de gestionar los tickets de soporte del sistema VetNova. Permite crear, listar, actualizar y eliminar soportes y sus mensajes asociados, con validación de duplicados por usuario y asunto.

## Integrantes
- Sebastián Miranda
- Vicente Provoste
- Bastián Chamblas

## Tecnologías
- Java 17
- Spring Boot 4.0.6
- MySQL
- OpenFeign (comunicación con Notificaciones y Reportes)
- JUnit 5 + Mockito
- Swagger / OpenAPI (springdoc-openapi)

## Endpoints principales
| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | /soportes | Crear soporte |
| GET | /soportes | Listar todos |
| GET | /soportes/{id} | Obtener por ID |
| PUT | /soportes/{id} | Actualizar |
| DELETE | /soportes/{id} | Eliminar |
| POST | /soportes/{id}/mensajes | Agregar mensaje |
| GET | /soportes/{id}/mensajes | Listar mensajes |
| DELETE | /soportes/{id}/mensajes/{msgId} | Eliminar mensaje |

## Documentación Swagger
- Local: http://localhost:8087/swagger-ui/index.html
- JSON: http://localhost:8087/v3/api-docs

## Ejecución local
1. Tener XAMPP corriendo con MySQL activo
2. Asegurarse de que la base de datos `soporte_db` existe
3. Ejecutar:
```bash
./mvnw spring-boot:run
```
4. El servicio estará disponible en el puerto **8087**

## Pruebas unitarias
```bash
./mvnw test
```