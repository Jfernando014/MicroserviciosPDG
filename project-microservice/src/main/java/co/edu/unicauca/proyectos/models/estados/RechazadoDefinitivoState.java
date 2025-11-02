package co.edu.unicauca.proyectos.models.estados;

import org.springframework.stereotype.Component;
import co.edu.unicauca.proyectos.models.EstadoProyecto;
import co.edu.unicauca.proyectos.models.ProyectoGrado;

@Component
public class RechazadoDefinitivoState implements EstadoProyecto {
    @Override
    public void evaluar(ProyectoGrado proyecto, boolean aprobado, String observaciones) {
        throw new IllegalStateException("El proyecto fue rechazado definitivamente.");
    }

    @Override
    public void reintentar(ProyectoGrado proyecto) {
        throw new IllegalStateException("No se pueden hacer más reintentos.");
    }

    @Override
    public String getNombreEstado() {
        return "RECHAZADO_DEFINITIVO";
    }
}
