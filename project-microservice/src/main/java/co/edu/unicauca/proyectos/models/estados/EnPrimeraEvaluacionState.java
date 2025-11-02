package co.edu.unicauca.proyectos.models.estados;

import co.edu.unicauca.proyectos.models.ProyectoGrado;
import co.edu.unicauca.proyectos.models.EstadoProyecto;
import org.springframework.stereotype.Component;


@Component
public class EnPrimeraEvaluacionState implements EstadoProyecto {
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
        throw new IllegalStateException("No se puede reintentar en primera evaluación.");
    }

    @Override
    public String getNombreEstado() {
        return "EN_PRIMERA_EVALUACION_FORMATO_A";
    }
}