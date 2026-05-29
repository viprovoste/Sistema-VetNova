# Microservicio de Notificaciones - VetNova

## Descripción
Microservicio encargado de gestionar las notificaciones de la clínica veterinaria VetNova. Recibe eventos del microservicio de Soporte y registra notificaciones en la base de datos.

## Integrantes
- Sebastián Miranda, Vicente Provoste, Marcel Chamblas

## Funcionalidades implementadas
- CRUD completo de notificaciones
- Recepción de notificaciones desde microservicio de Soporte
- Manejo centralizado de errores con @ControllerAdvice
- Validaciones con Bean Validation
- Logs estructurados con SLF4J

## Tecnologías
- Java 17
- Spring Boot 4.0.6
- Spring Data JPA + Hibernate
- MySQL
- Maven

## Pasos para ejecutar
1. Tener MySQL corriendo en localhost:3306
2. La base de datos `vetnova_notificaciones` se crea automáticamente
3. Clonar el repositorio
4. En la carpeta del proyecto ejecutar: