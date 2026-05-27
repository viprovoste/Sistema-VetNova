# Microservicio de Reportes - VetNova

## Descripción
Microservicio encargado de gestionar los reportes analíticos de la clínica veterinaria VetNova. Recibe datos del microservicio de Soporte y registra métricas como total de atenciones, alertas generadas y rendimiento global.

## Integrantes
- Sebastián Miranda, Bastian Chamblas, Vicente Provoste

## Funcionalidades implementadas
- CRUD completo de reportes
- Recepción de datos desde microservicio de Soporte
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
2. La base de datos `vetnova_reportes` se crea automáticamente
3. Clonar el repositorio
4. En la carpeta del proyecto ejecutar: