package co.edu.unicauca.project_microservice.service;

import co.edu.unicauca.project_microservice.entity.ProyectoGrado;
import java.util.List;

public interface IProjectServiceFacade {
    ProyectoGrado crearProyecto(ProyectoGrado proyecto);
    void evaluarProyecto(Long id, boolean aprobado, String observaciones);
    void reintentarProyecto(Long id);
    void subirAnteproyecto(Long idProyecto, String jefeDepartamentoEmail);
    List<ProyectoGrado> obtenerProyectosPorEstudiante(String email);
    List<ProyectoGrado> obtenerAnteproyectosPorJefe(String emailJefe);
}