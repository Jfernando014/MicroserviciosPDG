package co.edu.unicauca.proyectos.services;

import co.edu.unicauca.proyectos.models.ProyectoGrado;
import java.util.List;

public interface IProyectoServiceFacade {
    ProyectoGrado crearProyecto(ProyectoGrado proyecto);
    void evaluarProyecto(Long id, boolean aprobado, String observaciones);
    void reintentarProyecto(Long id);
    void subirAnteproyecto(Long idProyecto, String jefeDepartamentoEmail);
    List<ProyectoGrado> obtenerProyectosPorEstudiante(String email);
    List<ProyectoGrado> obtenerAnteproyectosPorJefe(String emailJefe);
}