# notification-microservice

Microservicio encargado de la gestión de notificaciones generadas por eventos del sistema de trabajos de grado.

## Funcionalidades

- Recepción de eventos desde otros microservicios (mensajes, documentos, proyectos).

- Registro y almacenamiento de notificaciones.
  
- Consulta de notificaciones por usuario.

- Integración con mensajería (RabbitMQ).

## Tecnologías

- Spring Boot 3

- Spring Data JPA + H2

- RabbitMQ (broker de eventos)

- Swagger UI para documentación

## Endpoints

- POST /api/notificaciones — Crear notificación

- GET /api/notificaciones/usuario/{email} — Listar notificaciones de un usuario

- DELETE /api/notificaciones/{id} — Eliminar notificación
