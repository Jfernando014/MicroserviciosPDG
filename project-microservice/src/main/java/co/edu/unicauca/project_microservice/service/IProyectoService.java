package co.edu.unicauca.project_microservice.service;

import co.edu.unicauca.project_microservice.entity.ProyectoGrado;
import java.util.List;

public interface IProyectoService {
    ProyectoGrado crear(ProyectoGrado proyecto);
    ProyectoGrado obtenerPorId(Long id);
    List<ProyectoGrado> findByEstudiante1Email(String email);
    List<ProyectoGrado> findByDirectorEmail(String email);
    List<ProyectoGrado> obtenerTodos();
    ProyectoGrado guardar(ProyectoGrado proyecto);
}
