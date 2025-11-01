package co.edu.unicauca.document_microservice.service;

import co.edu.unicauca.document_microservice.entity.Documento;
import co.edu.unicauca.document_microservice.factory.DocumentoFactory;
import co.edu.unicauca.document_microservice.infra.dto.DocumentoRequest;
import co.edu.unicauca.document_microservice.repository.DocumentoRepository;
import co.edu.unicauca.document_microservice.utilities.validator.DocumentoValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentoService implements IDocumentoService {

    @Autowired
    private DocumentoRepository documentoRepository;

    private final DocumentoFactory documentoFactory = DocumentoFactory.getInstance();

    @Value("${app.document.storage-dir}")
    private String storageDir;

    @Override
    public Documento subirDocumento(DocumentoRequest request) throws Exception {
        MultipartFile archivo = request.getArchivo();
        String tipoDocumento = request.getTipoDocumento();
        Long idProyecto = request.getIdProyecto();

        if (archivo.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío.");
        }

        // --- PATRÓN FACTORY METHOD ---
        DocumentoValidator validator = documentoFactory.crearValidator(tipoDocumento);
        validator.validar(archivo);
        // ------------------------------

        Path dirPath = Paths.get(storageDir);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        String originalFilename = archivo.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.lastIndexOf(".") > 0) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String nombreUnico = UUID.randomUUID() + extension;
        Path filePath = dirPath.resolve(nombreUnico);

        try {
            archivo.transferTo(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo en disco.", e);
        }

        Documento documento = new Documento();
        documento.setIdProyecto(idProyecto);
        documento.setTipoDocumento(tipoDocumento);
        documento.setNombreArchivo(originalFilename != null ? originalFilename : "archivo" + extension);
        documento.setExtension(extension);
        documento.setTamaño(archivo.getSize());
        documento.setRutaArchivo(filePath.toString());
        documento.setEstado("PENDIENTE");

        return documentoRepository.save(documento);
    }

    @Override
    public List<Documento> obtenerDocumentosPorProyecto(Long idProyecto) {
        return documentoRepository.findByIdProyecto(idProyecto);
    }

    @Override
    public List<Documento> obtenerDocumentosPorProyectoYTipo(Long idProyecto, String tipoDocumento) {
        return documentoRepository.findByIdProyectoAndTipoDocumento(idProyecto, tipoDocumento);
    }

    @Override
    public byte[] descargarDocumento(Long id) throws Exception {
        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado con ID: " + id));
        return Files.readAllBytes(Paths.get(documento.getRutaArchivo()));
    }
}