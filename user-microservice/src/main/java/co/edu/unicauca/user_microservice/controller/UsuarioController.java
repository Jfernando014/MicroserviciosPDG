package co.edu.unicauca.user_microservice.controller;

import co.edu.unicauca.user_microservice.entity.Coordinador;
import co.edu.unicauca.user_microservice.entity.Docente;
import co.edu.unicauca.user_microservice.entity.Estudiante;
import co.edu.unicauca.user_microservice.entity.JefeDepartamento;
import co.edu.unicauca.user_microservice.infra.dto.CoordinadorRequest;
import co.edu.unicauca.user_microservice.infra.dto.DocenteRequest;
import co.edu.unicauca.user_microservice.infra.dto.EstudianteRequest;
import co.edu.unicauca.user_microservice.infra.dto.JefeDepartamentoRequest;
import co.edu.unicauca.user_microservice.service.IUsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * Controlador REST para gestionar el registro y consulta de usuarios.
 * Soporta los roles: Docente, Estudiante, Coordinador y Jefe de Departamento.
 */
@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Gestión de Usuarios", description = "API para registrar y consultar usuarios del sistema")
public class UsuarioController {

    @Autowired
    private IUsuarioService usuarioService;

        @Operation(summary = "Validar existencia y rol de un usuario")
    @GetMapping("/validar")
    public ResponseEntity<Map<String, Object>> validarUsuario(@RequestParam String email) {
        try {
            boolean existe = usuarioService.existeUsuario(email);
            String rol = existe ? usuarioService.obtenerRol(email) : "DESCONOCIDO";
            Map<String, Object> respuesta = Map.of("existe", existe, "rol", rol);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }
    // ========== DOCENTES ==========
    @Operation(
        summary = "Registrar un nuevo docente",
        description = "Registra un docente en el sistema con validación de email institucional y contraseña fuerte.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del docente a registrar",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = DocenteRequest.class),
                examples = @ExampleObject(
                    name = "Ejemplo de docente",
                    value = """
                    {
                      "email": "juan.perez@unicauca.edu.co",
                      "password": "Pass123!",
                      "nombres": "Juan Carlos",
                      "apellidos": "Pérez Gómez",
                      "celular": "3101234567",
                      "programa": "INGENIERIA_SISTEMAS",
                      "tipoDocente": "PLANTA"
                    }
                    """
                )
            )
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Docente registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
        }
    )
    @PostMapping("/docentes")
    public ResponseEntity<?> registrarDocente(@RequestBody DocenteRequest request) {
        try {
            Docente docente = new Docente();
            docente.setEmail(request.getEmail());
            docente.setPassword(request.getPassword());
            docente.setNombres(request.getNombres());
            docente.setApellidos(request.getApellidos());
            docente.setCelular(request.getCelular());
            docente.setPrograma(request.getPrograma());
            docente.setTipoDocente(request.getTipoDocente());

            Docente resultado = (Docente) usuarioService.registrarDocente(docente);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // ========== ESTUDIANTES ==========
    @Operation(
        summary = "Registrar un nuevo estudiante",
        description = "Registra un estudiante en el sistema.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del estudiante a registrar",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EstudianteRequest.class),
                examples = @ExampleObject(
                    name = "Ejemplo de estudiante",
                    value = """
                    {
                      "email": "ana.gomez@unicauca.edu.co",
                      "password": "Stud123!",
                      "nombres": "Ana María",
                      "apellidos": "Gómez López",
                      "celular": "3209876543",
                      "programa": "INGENIERIA_SISTEMAS"
                    }
                    """
                )
            )
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Estudiante registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
        }
    )
    @PostMapping("/estudiantes")
    public ResponseEntity<?> registrarEstudiante(@RequestBody EstudianteRequest request) {
        try {
            Estudiante estudiante = new Estudiante();
            estudiante.setEmail(request.getEmail());
            estudiante.setPassword(request.getPassword());
            estudiante.setNombres(request.getNombres());
            estudiante.setApellidos(request.getApellidos());
            estudiante.setCelular(request.getCelular());
            estudiante.setPrograma(request.getPrograma());

            Estudiante resultado = (Estudiante) usuarioService.registrarEstudiante(estudiante);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // ========== COORDINADORES ==========
    @Operation(
        summary = "Registrar un nuevo coordinador",
        description = "Registra un coordinador de programa en el sistema.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del coordinador a registrar",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CoordinadorRequest.class),
                examples = @ExampleObject(
                    name = "Ejemplo de coordinador",
                    value = """
                    {
                      "email": "coordinador.sistemas@unicauca.edu.co",
                      "password": "Coord123!",
                      "nombres": "Carlos Alberto",
                      "apellidos": "Ramírez Ruiz",
                      "celular": "3151122334",
                      "programa": "INGENIERIA_SISTEMAS"
                    }
                    """
                )
            )
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Coordinador registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
        }
    )
    @PostMapping("/coordinadores")
    public ResponseEntity<?> registrarCoordinador(@RequestBody CoordinadorRequest request) {
        try {
            Coordinador coordinador = new Coordinador();
            coordinador.setEmail(request.getEmail());
            coordinador.setPassword(request.getPassword());
            coordinador.setNombres(request.getNombres());
            coordinador.setApellidos(request.getApellidos());
            coordinador.setCelular(request.getCelular());
            coordinador.setPrograma(request.getPrograma());

            Coordinador resultado = (Coordinador) usuarioService.registrarCoordinador(coordinador);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // ========== JEFES DE DEPARTAMENTO ==========
    @Operation(
        summary = "Registrar un nuevo jefe de departamento",
        description = "Registra un jefe de departamento en el sistema.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del jefe de departamento a registrar",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = JefeDepartamentoRequest.class),
                examples = @ExampleObject(
                    name = "Ejemplo de jefe de departamento",
                    value = """
                    {
                      "email": "jefe.sistemas@unicauca.edu.co",
                      "password": "Jefe123!",
                      "nombres": "María Fernanda",
                      "apellidos": "López Sánchez",
                      "celular": "3161122334",
                      "programa": "INGENIERIA_SISTEMAS"
                    }
                    """
                )
            )
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Jefe de departamento registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
        }
    )
    @PostMapping("/jefes-departamento")
    public ResponseEntity<?> registrarJefeDepartamento(@RequestBody JefeDepartamentoRequest request) {
        try {
            JefeDepartamento jefe = new JefeDepartamento();
            jefe.setEmail(request.getEmail());
            jefe.setPassword(request.getPassword());
            jefe.setNombres(request.getNombres());
            jefe.setApellidos(request.getApellidos());
            jefe.setCelular(request.getCelular());
            jefe.setPrograma(request.getPrograma());

            JefeDepartamento resultado = (JefeDepartamento) usuarioService.registrarJefeDepartamento(jefe);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // ========== CONSULTA GENÉRICA ==========
    @Operation(
        summary = "Obtener un usuario por email",
        description = "Recupera los datos de un usuario registrado usando su email.",
        parameters = {
            @io.swagger.v3.oas.annotations.Parameter(
                name = "email",
                description = "Email del usuario a consultar",
                required = true,
                example = "juan.perez@unicauca.edu.co"
            )
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
        }
    )
    @GetMapping("/{email}")
    public ResponseEntity<?> obtenerUsuario(@PathVariable String email) {
        try {
            var usuario = usuarioService.obtenerPorEmail(email);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}