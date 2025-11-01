package co.edu.unicauca.project_microservice.service.evaluacion;

import co.edu.unicauca.project_microservice.entity.ProyectoGrado;
import co.edu.unicauca.project_microservice.service.IProyectoService;
import co.edu.unicauca.project_microservice.service.INotificationServiceClient;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class EvaluadorProyecto {

    @Autowired
    protected IProyectoService proyectoService;

    @Autowired
    protected INotificationServiceClient notificationClient;

    public final void evaluarProyecto(Long idProyecto, boolean aprobado, String observaciones) {
        ProyectoGrado proyecto = obtenerProyecto(idProyecto);
        validarProyecto(proyecto);
        aplicarEvaluacion(proyecto, aprobado, observaciones);
        guardarProyecto(proyecto);
        enviarNotificacion(proyecto, aprobado, observaciones);
    }

    protected ProyectoGrado obtenerProyecto(Long id) {
        return proyectoService.obtenerPorId(id);
    }

    protected void validarProyecto(ProyectoGrado proyecto) {
        if (proyecto == null) throw new RuntimeException("Proyecto no encontrado.");
    }

    protected abstract void aplicarEvaluacion(ProyectoGrado proyecto, boolean aprobado, String observaciones);
    protected abstract void enviarNotificacion(ProyectoGrado proyecto, boolean aprobado, String observaciones);

    protected void guardarProyecto(ProyectoGrado proyecto) {
        proyectoService.guardar(proyecto);
    }
}