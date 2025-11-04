# document-microservice

Microservicio encargado de la gestión y almacenamiento de documentos asociados a los trabajos de grado dentro del sistema.

## Funcionalidades
- Carga y almacenamiento de documentos en el sistema.
- Descarga y eliminación de archivos.
- Asociación de documentos a proyectos de grado.
- Validación del tipo y tamaño de archivo.
- Persistencia de información de metadatos en base de datos.

## Tecnologías
- Spring Boot 3
- Spring Data JPA + H2 (modo archivo, persistente)
- Swagger UI para documentación

## Endpoints
- POST /api/documentos — Cargar un documento
- GET /api/documentos/{id} — Obtener documento por ID
- DELETE /api/documentos/{id} — Eliminar documento
- GET /api/documentos/proyecto/{idProyecto} — Listar documentos por proyecto

## Puerto
- `8083`
