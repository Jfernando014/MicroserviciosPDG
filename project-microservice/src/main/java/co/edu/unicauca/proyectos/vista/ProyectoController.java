package co.edu.unicauca.proyectos.vista;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import co.edu.unicauca.proyectos.models.ProyectoGrado;
import co.edu.unicauca.proyectos.dto.ProyectoRequest;
import co.edu.unicauca.proyectos.services.IProyectoServiceFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;


/**
 * Controlador REST para gestionar proyectos de grado.
 * Permite crear nuevos proyectos, evaluarlos y consultar el estado de los proyectos de un estudiante.
 */
@RestController
@RequestMapping("/api/proyectos")
@Tag(name = "Gestión de Proyectos de Grado", description = "API para crear, evaluar y consultar proyectos de grado")
public class ProyectoController {

    @Autowired
    private IProyectoServiceFacade facade;

    @Operation(
            summary = "Crear un nuevo proyecto de grado",
            description = "Registra un nuevo proyecto de grado en la base de datos y publica un mensaje en RabbitMQ " +
                    "para que el microservicio de notificaciones envíe un correo al coordinador.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del proyecto a registrar",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProyectoRequest.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo de proyecto",
                                    value = """
                                            {
                                              "titulo": "Sistema de Gestión de Bibliotecas",
                                              "modalidad": "INVESTIGACION",
                                              "directorEmail": "juan.perez@unicauca.edu.co",
                                              "codirectorEmail": "coordinador.sistemas@unicauca.edu.co",
                                              "estudiante1Email": "ana.gomez@unicauca.edu.co",
                                              "estudiante2Email": "carlos.martinez@unicauca.edu.co",
                                              "objetivoGeneral": "Desarrollar un sistema...",
                                              "objetivosEspecificos": "1. Diseñar... 2. Implementar..."
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Proyecto creado exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProyectoGrado.class),
                                    examples = @ExampleObject(
                                            name = "Respuesta exitosa",
                                            value = """
                                                    {
                                                      "id": 1,
                                                      "titulo": "Sistema de Gestión de Bibliotecas",
                                                      "modalidad": "INVESTIGACION",
                                                      "directorEmail": "juan.perez@unicauca.edu.co",
                                                      "codirectorEmail": "coordinador.sistemas@unicauca.edu.co",
                                                      "estudiante1Email": "ana.gomez@unicauca.edu.co",
                                                      "estudiante2Email": "carlos.martinez@unicauca.edu.co",
                                                      "objetivoGeneral": "Desarrollar un sistema...",
                                                      "objetivosEspecificos": "1. Diseñar... 2. Implementar...",
                                                      "estadoActual": "EN_PRIMERA_EVALUACION_FORMATO_A"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Solicitud inválida (faltan campos obligatorios)",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "Error de validación",
                                            value = """
                                                    {"error": "Campos obligatorios faltantes"}
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno del servidor",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "Error interno",
                                            value = """
                                                    {"error": "Error interno"}
                                                    """
                                    )
                            )
                    )
            }
    )
    @PostMapping
    public ResponseEntity<?> crearProyecto(@RequestBody ProyectoRequest request) {
        try {
            ProyectoGrado proyecto = new ProyectoGrado();
            proyecto.setTitulo(request.getTitulo());
            proyecto.setModalidad(request.getModalidad());
            proyecto.setDirectorEmail(request.getDirectorEmail());
            proyecto.setCodirectorEmail(request.getCodirectorEmail());
            proyecto.setEstudiante1Email(request.getEstudiante1Email());
            proyecto.setEstudiante2Email(request.getEstudiante2Email());
            proyecto.setObjetivoGeneral(request.getObjetivoGeneral());
            proyecto.setObjetivosEspecificos(request.getObjetivosEspecificos());

            ProyectoGrado resultado = facade.crearProyecto(proyecto);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"Error interno\"}");
        }
    }

    @Operation(
            summary = "Subir Formato A",
            description = "Recibe el PDF del Formato A y la carta (obligatoria si la modalidad es PRACTICA_PROFESIONAL)."
    )
    @PostMapping(value = "/formatoA", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> subirFormatoA(
            @RequestParam String titulo,
            @RequestParam String modalidad, // INVESTIGACION | PRACTICA_PROFESIONAL
            @RequestParam String directorEmail,
            @RequestParam(required = false) String codirectorEmail,
            @RequestParam String estudiante1Email,
            @RequestPart("pdf") MultipartFile pdf,
            @RequestPart(value = "carta", required = false) MultipartFile carta
    ) {
        return facade.subirFormatoA(titulo, modalidad, directorEmail, codirectorEmail, estudiante1Email, pdf, carta);
    }


    @Operation(
            summary = "Evaluar un proyecto de grado",
            description = "Cambia el estado de un proyecto de grado a aprobado o rechazado y envía una notificación a los implicados.",
            parameters = {
                    @io.swagger.v3.oas.annotations.Parameter(
                            name = "id",
                            description = "ID del proyecto a evaluar",
                            required = true,
                            example = "1"
                    ),
                    @io.swagger.v3.oas.annotations.Parameter(
                            name = "aprobado",
                            description = "Indica si el proyecto es aprobado (true) o rechazado (false)",
                            required = true,
                            example = "true"
                    ),
                    @io.swagger.v3.oas.annotations.Parameter(
                            name = "observaciones",
                            description = "Observaciones del evaluador",
                            required = true,
                            example = "El proyecto cumple con todos los requisitos."
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Proyecto evaluado exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "Evaluación exitosa",
                                            value = """
                                                    {"mensaje": "Proyecto evaluado correctamente"}
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Proyecto no encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "No encontrado",
                                            value = """
                                                    {"error": "Proyecto no encontrado con ID: 999"}
                                                    """
                                    )
                            )
                    )
            }
    )
    @PostMapping("/{id}/evaluar")
    public ResponseEntity<?> evaluarProyecto(@PathVariable Long id, @RequestParam boolean aprobado, @RequestParam String observaciones) {
        try {
            facade.evaluarProyecto(id, aprobado, observaciones);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(404).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Operation(
            summary = "Subir anteproyecto",
            description = "Permite al docente subir el archivo PDF del anteproyecto una vez el Formato A ha sido aprobado. " +
                    "Envía notificación al jefe de departamento para asignar evaluadores.",
            parameters = {
                    @io.swagger.v3.oas.annotations.Parameter(
                            name = "idProyecto",
                            description = "ID del proyecto de grado",
                            required = true,
                            example = "1"
                    ),
                    @io.swagger.v3.oas.annotations.Parameter(
                            name = "jefeDepartamentoEmail",
                            description = "Correo del jefe de departamento que recibirá la notificación",
                            required = true,
                            example = "jefe.sistemas@unicauca.edu.co"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Anteproyecto subido exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "Respuesta exitosa",
                                            value = """
                                                    {
                                                      "mensaje": "Anteproyecto subido correctamente",
                                                      "idProyecto": 1,
                                                      "anteproyectoToken": "12345"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "El Formato A no está aprobado o faltan datos",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "Error de validación",
                                            value = """
                                                    {"error": "Solo se puede subir anteproyecto si el Formato A está aprobado."}
                                                    """
                                    )
                            )
                    )
            }
    )
    @PostMapping(value = "/{idProyecto}/anteproyecto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> subirAnteproyecto(
            @PathVariable Long idProyecto,
            @RequestParam String jefeDepartamentoEmail,
            @RequestPart("pdf") MultipartFile anteproyectoPdf
    ) {
        try {
            facade.subirAnteproyecto(idProyecto, jefeDepartamentoEmail, anteproyectoPdf);

            java.util.Map<String, Object> respuesta = new java.util.HashMap<>();
            respuesta.put("mensaje", "Anteproyecto subido correctamente");
            respuesta.put("idProyecto", idProyecto);

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Obtener proyectos por estudiante",
            description = "Recupera todos los proyectos asociados a un estudiante específico.",
            parameters = {
                    @io.swagger.v3.oas.annotations.Parameter(
                            name = "email",
                            description = "Correo del estudiante",
                            required = true,
                            example = "ana.gomez@unicauca.edu.co"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de proyectos encontrada",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProyectoGrado.class),
                                    examples = @ExampleObject(
                                            name = "Lista de proyectos",
                                            value = """
                                                    [
                                                      {
                                                        "id": 1,
                                                        "titulo": "Sistema de Gestión de Bibliotecas",
                                                        "modalidad": "INVESTIGACION",
                                                        "directorEmail": "juan.perez@unicauca.edu.co",
                                                        "estudiante1Email": "ana.gomez@unicauca.edu.co",
                                                        "estadoActual": "EN_PRIMERA_EVALUACION_FORMATO_A"
                                                      }
                                                    ]
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Estudiante no encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "No encontrado",
                                            value = """
                                                    {"error": "Estudiante no encontrado"}
                                                    """
                                    )
                            )
                    )
            }
    )
    @GetMapping("/estudiante/{email}")
    public ResponseEntity<?> obtenerPorEstudiante(@PathVariable String email) {
        try {
            List<ProyectoGrado> proyectos = facade.obtenerProyectosPorEstudiante(email);
            return ResponseEntity.ok(proyectos);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Operation(
            summary = "Obtener anteproyectos para evaluación (Jefe de Departamento)",
            description = "Recupera todos los proyectos que tienen anteproyecto subido y están pendientes de asignación de evaluadores.",
            parameters = {
                    @io.swagger.v3.oas.annotations.Parameter(
                            name = "emailJefe",
                            description = "Correo del jefe de departamento",
                            required = true,
                            example = "jefe.sistemas@unicauca.edu.co"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de proyectos con anteproyecto",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProyectoGrado.class)
                            )
                    )
            }
    )
    @GetMapping("/anteproyectos/jefe/{emailJefe}")
    public ResponseEntity<?> obtenerAnteproyectosPorJefe(@PathVariable String emailJefe) {
        try {
            List<ProyectoGrado> proyectos = facade.obtenerAnteproyectosPorJefe(emailJefe);
            return ResponseEntity.ok(proyectos);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Reintentar un proyecto de grado",
            description = "Permite subir una nueva versión del Formato A tras un rechazo.",
            parameters = {
                    @io.swagger.v3.oas.annotations.Parameter(
                            name = "id",
                            description = "ID del proyecto a reintentar",
                            required = true,
                            example = "1"
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Reintento procesado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "No se puede reintentar (ej: ya aprobado o 3 intentos)"),
                    @ApiResponse(responseCode = "404", description = "Proyecto no encontrado")
            }
    )
    @PostMapping("/{id}/reintentar")
    public ResponseEntity<?> reintentarProyecto(@PathVariable Long id) {
        try {
            facade.reintentarProyecto(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Operation(
        summary = "Obtener proyecto por ID",
        description = "Recupera los detalles completos de un proyecto específico por su identificador.",
        parameters = {
            @io.swagger.v3.oas.annotations.Parameter(
                name = "id",
                description = "ID del proyecto",
                required = true,
                example = "1"
            )
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Proyecto encontrado",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProyectoGrado.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Proyecto no encontrado"
            )
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerProyectoPorId(@PathVariable Long id) {
        try {
            ProyectoGrado proyecto = facade.obtenerProyectosPorEstudiante(null)
                    .stream()
                    .filter(p -> p.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
            
            return ResponseEntity.ok(proyecto);
        } catch (Exception e) {
            return ResponseEntity.status(404)
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }
}
