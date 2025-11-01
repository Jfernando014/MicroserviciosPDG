package co.edu.unicauca.project_microservice.entity.estados;

import org.springframework.stereotype.Component;
import co.edu.unicauca.project_microservice.entity.EstadoProyecto;
import co.edu.unicauca.project_microservice.entity.ProyectoGrado;

@Component
public class EnTerceraEvaluacionState implements EstadoProyecto {
    @Override
    public void evaluar(ProyectoGrado proyecto, boolean aprobado, String observaciones) {
        proyecto.setObservacionesEvaluacion(observaciones);
        if (aprobado) {
            proyecto.setEstado(new FormatoAAprobadoState());
        } else {
            proyecto.setEstado(new FormatoARechazadoState());
        }
    }

    @Override
    public void reintentar(ProyectoGrado proyecto) {
        throw new IllegalStateException("No se puede reintentar en tercera evaluación.");
    }

    @Override
    public String getNombreEstado() {
        return "EN_TERCERA_EVALUACION_FORMATO_A";
    }
}