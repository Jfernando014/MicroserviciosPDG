package co.edu.unicauca.project_microservice.entity;

public interface EstadoProyecto {
    void evaluar(ProyectoGrado proyecto, boolean aprobado, String observaciones);
    void reintentar(ProyectoGrado proyecto);
    String getNombreEstado();
}
