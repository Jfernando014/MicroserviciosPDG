package co.edu.unicauca.messaging_microservice.service;

import co.edu.unicauca.messaging_microservice.entity.MensajeInterno;
import co.edu.unicauca.messaging_microservice.infra.dto.MensajeInternoRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IMensajeInternoService {
    Long enviarMensaje(MensajeInternoRequest request) throws Exception;
    List<MensajeInterno> obtenerMensajesEnviadosPorEstudiante(String emailEstudiante);
    List<MensajeInterno> obtenerMensajesRecibidosPorDocente(String emailDocente);
    boolean marcarComoLeido(Long idMensaje);
}