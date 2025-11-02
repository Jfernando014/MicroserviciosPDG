package co.edu.unicauca.proyectos.services;

import co.edu.unicauca.proyectos.models.ProyectoGrado;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface IProyectoServiceFacade {
    
    ResponseEntity<?> subirFormatoA(
            String titulo,
            String modalidad,
            String directorEmail,
            String codirectorEmail,
            String estudiante1Email,
            MultipartFile pdf,
            MultipartFile carta
    );
    
    ProyectoGrado crearProyecto(ProyectoGrado proyecto);
    
    void evaluarProyecto(Long id, boolean aprobado, String observaciones);
    
    void reintentarProyecto(Long id);

    // Método actualizado: ahora recibe el archivo PDF del anteproyecto
    ResponseEntity<?> subirAnteproyecto(Long idProyecto, String jefeDepartamentoEmail, MultipartFile anteproyectoPdf);

    List<ProyectoGrado> obtenerProyectosPorEstudiante(String email);

    List<ProyectoGrado> obtenerAnteproyectosPorJefe(String emailJefe);

    // Nuevos métodos para los endpoints faltantes
    ProyectoGrado obtenerProyectoPorId(Long id);

    List<ProyectoGrado> obtenerTodosProyectos();
}

