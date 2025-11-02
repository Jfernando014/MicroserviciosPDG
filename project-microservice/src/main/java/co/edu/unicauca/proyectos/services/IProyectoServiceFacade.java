package co.edu.unicauca.proyectos.services;

import co.edu.unicauca.proyectos.models.ProyectoGrado;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface IProyectoServiceFacade {
    ProyectoGrado crearProyecto(ProyectoGrado proyecto);
    void evaluarProyecto(Long id, boolean aprobado, String observaciones);
    void reintentarProyecto(Long id);
    void subirAnteproyecto(Long idProyecto, String jefeDepartamentoEmail);
    List<ProyectoGrado> obtenerProyectosPorEstudiante(String email);
    List<ProyectoGrado> obtenerAnteproyectosPorJefe(String emailJefe);

    ResponseEntity<?> subirFormatoA(
            String titulo,
            String modalidad,
            String directorEmail,
            String codirectorEmail,
            String estudiante1Email,
            MultipartFile pdf,
            MultipartFile carta
    );
}

