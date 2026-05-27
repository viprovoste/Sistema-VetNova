# Microservicio de Soporte - VetNova

## Descripción
Microservicio encargado de gestionar los tickets de soporte técnico de la clínica veterinaria VetNova. Permite crear, consultar y eliminar tickets de soporte, así como agregar mensajes de seguimiento a cada ticket. Se comunica con los microservicios de Notificaciones y Reportes mediante Feign Client.

## Integrantes
- Sebastián Miranda, Vicente Provoste, Marcel Chamblas

## Funcionalidades implementadas
- CRUD completo de tickets de soporte
- Gestión de mensajes por ticket (relación OneToMany)
- Comunicación con microservicio de Notificaciones vía Feign Client
- Comunicación con microservicio de Reportes vía Feign Client
- Manejo centralizado de errores con @ControllerAdvice
- Validaciones con Bean Validation
- Logs estructurados con SLF4J

## Tecnologías
- Java 17
- Spring Boot 4.0.6
- Spring Data JPA + Hibernate
- MySQL
- Spring Cloud OpenFeign
- Maven

## Pasos para ejecutar
1. Tener MySQL corriendo en localhost:3306
2. La base de datos `soporte_db` se crea automáticamente
3. Clonar el repositorio
4. En la carpeta del proyecto ejecutar: