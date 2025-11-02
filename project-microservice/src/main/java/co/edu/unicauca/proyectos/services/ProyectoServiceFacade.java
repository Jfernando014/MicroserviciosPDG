package co.edu.unicauca.proyectos.services;

import co.edu.unicauca.proyectos.repository.ProyectoRepository;
import co.edu.unicauca.proyectos.services.clients.UsuariosClient;
import co.edu.unicauca.proyectos.services.clients.DocumentosClient;
import co.edu.unicauca.proyectos.models.ProyectoGrado;
import co.edu.unicauca.proyectos.models.estados.EnPrimeraEvaluacionState;
import co.edu.unicauca.proyectos.dto.*; // FormatoASubidoEvent, AnteproyectoSubidoEvent
import co.edu.unicauca.proyectos.services.evaluacion.EvaluadorAprobacion;
import co.edu.unicauca.proyectos.services.evaluacion.EvaluadorRechazo;

import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;
import java.util.Map;

@Service
public class ProyectoServiceFacade implements IProyectoServiceFacade {

    // Constantes de mensajería
    private static final String EXCHANGE = "notificaciones.exchange";
    private static final String RK_FORMATO_A_SUBIDO = "formatoA.subido";
    private static final String RK_ANTEPROYECTO_SUBIDO = "anteproyecto.subido";

    // Dependencias
    private final ProyectoRepository proyectoRepository;
    private final IProyectoService proyectoService;
    private final EnPrimeraEvaluacionState enPrimeraEvaluacionState;
    private final EvaluadorAprobacion evaluadorAprobacion;
    private final EvaluadorRechazo evaluadorRechazo;
    private final UsuariosClient userClient;
    private final DocumentosClient documentosClient;
    private final RabbitTemplate rabbitTemplate;

    public ProyectoServiceFacade(ProyectoRepository proyectoRepository,
                                 IProyectoService proyectoService,
                                 EnPrimeraEvaluacionState enPrimeraEvaluacionState,
                                 EvaluadorAprobacion evaluadorAprobacion,
                                 EvaluadorRechazo evaluadorRechazo,
                                 UsuariosClient userClient,
                                 DocumentosClient documentosClient,
                                 RabbitTemplate rabbitTemplate) {
        this.proyectoRepository = proyectoRepository;
        this.proyectoService = proyectoService;
        this.enPrimeraEvaluacionState = enPrimeraEvaluacionState;
        this.evaluadorAprobacion = evaluadorAprobacion;
        this.evaluadorRechazo = evaluadorRechazo;
        this.userClient = userClient;
        this.documentosClient = documentosClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public ResponseEntity<?> subirFormatoA(
            String titulo,
            String modalidad,
            String directorEmail,
            String codirectorEmail,
            String estudiante1Email,
            MultipartFile pdf,
            MultipartFile carta
    ){
        if ("PRACTICA_PROFESIONAL".equalsIgnoreCase(modalidad) && (carta == null || carta.isEmpty())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Se requiere carta de aceptación"));
        }

        // Cambiar "formatoA" por "FORMATO_A" y "cartas" por "CARTA_EMPRESA"
        String formatoTok = documentosClient.subir(0L, "FORMATO_A", pdf);
        String cartaTok = (carta != null && !carta.isEmpty()) ? 
                          documentosClient.subir(0L, "CARTA_EMPRESA", carta) : null;

        ProyectoGrado p = new ProyectoGrado();
        p.setTitulo(titulo);
        p.setModalidad(modalidad);
        p.setDirectorEmail(directorEmail);
        p.setCodirectorEmail(codirectorEmail);
        p.setEstudiante1Email(estudiante1Email);
        p.setFormatoAToken(formatoTok);
        p.setCartaToken(cartaTok);

        p = proyectoRepository.save(p);

        // Evento a notificaciones
        FormatoASubidoEvent ev = new FormatoASubidoEvent();
        ev.setIdProyecto(p.getId());
        ev.setTitulo(p.getTitulo());
        ev.setCoordinadorEmail("coordinador.sistemas@unicauca.edu.co");
        rabbitTemplate.convertAndSend(EXCHANGE, RK_FORMATO_A_SUBIDO, ev);

        Map<String, Object> respuesta = new java.util.HashMap<>();
        respuesta.put("idProyecto", p.getId());
        respuesta.put("formatoAToken", formatoTok);
        if (cartaTok != null) {
            respuesta.put("cartaToken", cartaTok);
        }
        
        return ResponseEntity.ok(respuesta);
    }

    private void validarUsuario(String email, String rolEsperado) {
        Map<String, Object> r = userClient.validarUsuario(email);
        Boolean existe = (Boolean) r.get("existe");
        String rol = (String) r.get("rol");
        if (!Boolean.TRUE.equals(existe)) throw new RuntimeException("El usuario " + email + " no existe.");
        if (!rolEsperado.equals(rol)) throw new RuntimeException("El usuario " + email + " no es " + rolEsperado + ".");
    }

    @Override
    public ProyectoGrado crearProyecto(ProyectoGrado proyecto) {
        validarUsuario(proyecto.getDirectorEmail(), "DOCENTE");
        if (proyecto.getCodirectorEmail()!=null && !proyecto.getCodirectorEmail().isEmpty())
            validarUsuario(proyecto.getCodirectorEmail(), "DOCENTE");
        validarUsuario(proyecto.getEstudiante1Email(), "ESTUDIANTE");
        if (proyecto.getEstudiante2Email()!=null && !proyecto.getEstudiante2Email().isEmpty())
            validarUsuario(proyecto.getEstudiante2Email(), "ESTUDIANTE");

        proyecto.setEstado(enPrimeraEvaluacionState);
        ProyectoGrado guardado = proyectoService.crear(proyecto);

        FormatoASubidoEvent ev = new FormatoASubidoEvent();
        ev.setIdProyecto(guardado.getId());
        ev.setTitulo(guardado.getTitulo());
        ev.setCoordinadorEmail("coordinador.sistemas@unicauca.edu.co");
        rabbitTemplate.convertAndSend(EXCHANGE, RK_FORMATO_A_SUBIDO, ev);

        return guardado;
    }

    @Override
    public void evaluarProyecto(Long id, boolean aprobado, String observaciones) {
        if (aprobado) evaluadorAprobacion.evaluarProyecto(id, true, observaciones);
        else evaluadorRechazo.evaluarProyecto(id, false, observaciones);
    }

    @Override
    public void reintentarProyecto(Long id) {
        ProyectoGrado p = proyectoService.obtenerPorId(id);
        p.reintentar();
        proyectoService.guardar(p);

        // re-notificar coordinador
        FormatoASubidoEvent ev = new FormatoASubidoEvent();
        ev.setIdProyecto(p.getId());
        ev.setTitulo(p.getTitulo());
        ev.setCoordinadorEmail("coordinador.sistemas@unicauca.edu.co");
        rabbitTemplate.convertAndSend(EXCHANGE, RK_FORMATO_A_SUBIDO, ev);
    }

    @Override
    public void subirAnteproyecto(Long idProyecto, String jefeDepartamentoEmail) {
        ProyectoGrado p = proyectoService.obtenerPorId(idProyecto);
        if (!"FORMATO_A_APROBADO".equals(p.getEstadoActual())) {
            throw new RuntimeException("Solo se puede subir anteproyecto si el Formato A está aprobado.");
        }
        AnteproyectoSubidoEvent ev = new AnteproyectoSubidoEvent();
        ev.setIdProyecto(p.getId());
        ev.setTitulo(p.getTitulo());
        ev.setJefeDepartamentoEmail(jefeDepartamentoEmail);
        ev.setEstudianteEmail(p.getEstudiante1Email());
        ev.setTutor1Email(p.getDirectorEmail());
        if (p.getCodirectorEmail()!=null && !p.getCodirectorEmail().isEmpty())
            ev.setTutor2Email(p.getCodirectorEmail());
        rabbitTemplate.convertAndSend(EXCHANGE, RK_ANTEPROYECTO_SUBIDO, ev);
    }

    @Override
    public List<ProyectoGrado> obtenerProyectosPorEstudiante(String email) {
        return proyectoService.findByEstudiante1Email(email);
    }

    @Override
    public List<ProyectoGrado> obtenerAnteproyectosPorJefe(String emailJefe) {
        return proyectoService.obtenerTodos();
    }
}
