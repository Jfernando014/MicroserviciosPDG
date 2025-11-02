package co.edu.unicauca.proyectos.services.evaluacion;

import co.edu.unicauca.proyectos.models.ProyectoGrado;
import co.edu.unicauca.proyectos.models.estados.FormatoAAprobadoState;
import co.edu.unicauca.proyectos.dto.ProyectoEvaluadoEvent;
import org.springframework.stereotype.Component;

@Component
public class EvaluadorAprobacion extends EvaluadorProyecto {

    @Override
    protected void aplicarEvaluacion(ProyectoGrado proyecto, boolean aprobado, String observaciones) {
        proyecto.setObservacionesEvaluacion(observaciones);
        proyecto.setEstado(new FormatoAAprobadoState());
    }

    @Override
    protected void enviarNotificacion(ProyectoGrado proyecto, boolean aprobado, String observaciones) {
        ProyectoEvaluadoEvent event = new ProyectoEvaluadoEvent();
        event.setIdProyecto(proyecto.getId());
        event.setAprobado(true);
        event.setObservaciones(observaciones);
        event.setDestinatarios(new String[]{
            proyecto.getDirectorEmail(),
            proyecto.getEstudiante1Email()
        });
        notificationClient.notificarEvaluacion(event);
    }
}