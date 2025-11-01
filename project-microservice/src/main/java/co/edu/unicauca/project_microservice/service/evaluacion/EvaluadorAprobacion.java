package co.edu.unicauca.project_microservice.service.evaluacion;

import co.edu.unicauca.project_microservice.entity.ProyectoGrado;
import co.edu.unicauca.project_microservice.entity.estados.FormatoAAprobadoState;
import co.edu.unicauca.project_microservice.infra.dto.ProyectoEvaluadoEvent;
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