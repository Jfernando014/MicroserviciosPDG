package co.edu.unicauca.document_microservice.controller;

import co.edu.unicauca.document_microservice.entity.Documento;
import co.edu.unicauca.document_microservice.infra.dto.DocumentoRequest;
import co.edu.unicauca.document_microservice.service.IDocumentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/documentos")
@Tag(name = "Gestión de Documentos", description = "API para subir y descargar documentos de proyectos de grado")
public class DocumentoController {

    @Autowired
    private IDocumentoService documentoService;

    @Operation(summary = "Subir un documento")
    @PostMapping("/subir")
    public ResponseEntity<Documento> subirDocumento(@RequestParam("idProyecto") Long idProyecto,
                                                    @RequestParam("tipoDocumento") String tipoDocumento,
                                                    @RequestParam("archivo") MultipartFile archivo) {
        try {
            DocumentoRequest request = new DocumentoRequest();
            request.setIdProyecto(idProyecto);
            request.setTipoDocumento(tipoDocumento);
            request.setArchivo(archivo);
            Documento documento = documentoService.subirDocumento(request);
            return ResponseEntity.ok(documento);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Descargar un documento")
    @GetMapping("/descargar/{id}")
    public ResponseEntity<Resource> descargarDocumento(@PathVariable Long id) {
        try {
            byte[] contenido = documentoService.descargarDocumento(id);
            ByteArrayResource resource = new ByteArrayResource(contenido);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=documento.pdf")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Obtener documentos de un proyecto")
    @GetMapping("/proyecto/{idProyecto}")
    public ResponseEntity<List<Documento>> obtenerPorProyecto(@PathVariable Long idProyecto) {
        List<Documento> documentos = documentoService.obtenerDocumentosPorProyecto(idProyecto);
        return ResponseEntity.ok(documentos);
    }

    @Operation(summary = "Descargar plantilla del Formato A")
    @GetMapping("/plantilla/formato-a")
    public ResponseEntity<Resource> descargarPlantillaFormatoA() {
        try {
            // Ruta del archivo en resources/static/
            Path path = Paths.get("src/main/resources/static/formatoA.doc");
            // Si usas JAR, usa classpath:
            // Resource resource = new ClassPathResource("static/formato_a_plantilla.doc");

            UrlResource resource = new UrlResource(path.toUri());
            if (resource.exists()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=formato_a_plantilla.doc")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}