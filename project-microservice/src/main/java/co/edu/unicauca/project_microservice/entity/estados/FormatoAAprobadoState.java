package co.edu.unicauca.project_microservice.entity.estados;

import org.springframework.stereotype.Component;
import co.edu.unicauca.project_microservice.entity.EstadoProyecto;
import co.edu.unicauca.project_microservice.entity.ProyectoGrado;

@Component
public class FormatoAAprobadoState implements EstadoProyecto {
    @Override
    public void evaluar(ProyectoGrado proyecto, boolean aprobado, String observaciones) {
        throw new IllegalStateException("El proyecto ya fue aprobado.");
    }

    @Override
    public void reintentar(ProyectoGrado proyecto) {
        throw new IllegalStateException("No se puede reintentar un proyecto aprobado.");
    }

    @Override
    public String getNombreEstado() {
        return "FORMATO_A_APROBADO";
    }
}
