package co.edu.unicauca.project_microservice.service;

import co.edu.unicauca.project_microservice.Client.UserClient;
import co.edu.unicauca.project_microservice.entity.ProyectoGrado;
import co.edu.unicauca.project_microservice.entity.estados.EnPrimeraEvaluacionState;
import co.edu.unicauca.project_microservice.infra.dto.*;
import co.edu.unicauca.project_microservice.service.evaluacion.EvaluadorAprobacion;
import co.edu.unicauca.project_microservice.service.evaluacion.EvaluadorRechazo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class ProjectServiceFacade implements IProjectServiceFacade {


    @Autowired private IProyectoService proyectoService;
    @Autowired private INotificationServiceClient notificationClient;
    @Autowired private EnPrimeraEvaluacionState enPrimeraEvaluacionState;
    @Autowired private EvaluadorAprobacion evaluadorAprobacion;
    @Autowired private EvaluadorRechazo evaluadorRechazo;
    @Autowired private UserClient userClient; // 👈 Inyecta Feign Client

    private void validarUsuario(String email, String rolEsperado) {
        try {
            Map<String, Object> respuesta = userClient.validarUsuario(email); // 👈 Usa Feign
            Boolean existe = (Boolean) respuesta.get("existe");
            String rol = (String) respuesta.get("rol");

            if (!Boolean.TRUE.equals(existe)) {
                throw new RuntimeException("El usuario con email " + email + " no existe.");
            }
            if (!rol.equals(rolEsperado)) {
                throw new RuntimeException("El usuario con email " + email + " no es un " + rolEsperado + ".");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al validar el usuario " + email + ": " + e.getMessage());
        }
    }

    @Override
    public ProyectoGrado crearProyecto(ProyectoGrado proyecto) {
        validarUsuario(proyecto.getDirectorEmail(), "DOCENTE");
        if (proyecto.getCodirectorEmail() != null && !proyecto.getCodirectorEmail().isEmpty()) {
            validarUsuario(proyecto.getCodirectorEmail(), "DOCENTE");
        }
        validarUsuario(proyecto.getEstudiante1Email(), "ESTUDIANTE");
        if (proyecto.getEstudiante2Email() != null && !proyecto.getEstudiante2Email().isEmpty()) {
            validarUsuario(proyecto.getEstudiante2Email(), "ESTUDIANTE");
        }

        proyecto.setEstado(enPrimeraEvaluacionState);
        ProyectoGrado guardado = proyectoService.crear(proyecto);

        FormatoASubidoEvent event = new FormatoASubidoEvent();
        event.setIdProyecto(guardado.getId());
        event.setTitulo(guardado.getTitulo());
        event.setCoordinadorEmail("coordinador.sistemas@unicauca.edu.co"); // Hardcodeado por simplicidad
        notificationClient.notificarFormatoASubido(event);

        return guardado;
    }

    @Override
    public void evaluarProyecto(Long id, boolean aprobado, String observaciones) {
        if (aprobado) {
            evaluadorAprobacion.evaluarProyecto(id, true, observaciones);
        } else {
            evaluadorRechazo.evaluarProyecto(id, false, observaciones);
        }
    }

    @Override
    public void reintentarProyecto(Long id) {
        ProyectoGrado p = proyectoService.obtenerPorId(id);
        p.reintentar();
        proyectoService.guardar(p);
    }

    @Override
    public void subirAnteproyecto(Long idProyecto, String jefeDepartamentoEmail) {
        ProyectoGrado p = proyectoService.obtenerPorId(idProyecto);
        if (!"FORMATO_A_APROBADO".equals(p.getEstadoActual())) {
            throw new RuntimeException("Solo se puede subir anteproyecto si el Formato A está aprobado.");
        }

        AnteproyectoSubidoEvent event = new AnteproyectoSubidoEvent();
        event.setIdProyecto(p.getId());
        event.setTitulo(p.getTitulo());
        event.setJefeDepartamentoEmail(jefeDepartamentoEmail);
        event.setEstudianteEmail(p.getEstudiante1Email());
        event.setTutor1Email(p.getDirectorEmail());
        if (p.getCodirectorEmail() != null && !p.getCodirectorEmail().isEmpty()) {
            event.setTutor2Email(p.getCodirectorEmail());
        }
        notificationClient.notificarAnteproyectoSubido(event);
    }

    @Override
    public List<ProyectoGrado> obtenerProyectosPorEstudiante(String email) {
        return proyectoService.findByEstudiante1Email(email);
    }

    @Override
    public List<ProyectoGrado> obtenerAnteproyectosPorJefe(String emailJefe) {
        // En implementación real: filtrar por programa del jefe
        return proyectoService.obtenerTodos();
    }
}