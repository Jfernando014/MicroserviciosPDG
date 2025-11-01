package co.edu.unicauca.document_microservice.service;

import java.util.List;
import co.edu.unicauca.document_microservice.entity.Documento;
import co.edu.unicauca.document_microservice.infra.dto.DocumentoRequest;

public interface IDocumentoService {
    Documento subirDocumento(DocumentoRequest request) throws Exception;
    List<Documento> obtenerDocumentosPorProyecto(Long idProyecto);
    List<Documento> obtenerDocumentosPorProyectoYTipo(Long idProyecto, String tipoDocumento);
    byte[] descargarDocumento(Long id) throws Exception;
}
