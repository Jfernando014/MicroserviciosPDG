package co.edu.unicauca.document_microservice.repository;

import co.edu.unicauca.document_microservice.entity.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {
    List<Documento> findByIdProyectoAndTipoDocumento(Long idProyecto, String tipoDocumento);
    List<Documento> findByIdProyecto(Long idProyecto);
}