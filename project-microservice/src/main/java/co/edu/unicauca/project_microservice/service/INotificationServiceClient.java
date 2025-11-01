package co.edu.unicauca.project_microservice.service;

import co.edu.unicauca.project_microservice.infra.dto.FormatoASubidoEvent;
import co.edu.unicauca.project_microservice.infra.dto.ProyectoEvaluadoEvent;
import co.edu.unicauca.project_microservice.infra.dto.AnteproyectoSubidoEvent;

public interface INotificationServiceClient {
    void notificarFormatoASubido(FormatoASubidoEvent event);
    void notificarEvaluacion(ProyectoEvaluadoEvent event);
    void notificarAnteproyectoSubido(AnteproyectoSubidoEvent event);
}
