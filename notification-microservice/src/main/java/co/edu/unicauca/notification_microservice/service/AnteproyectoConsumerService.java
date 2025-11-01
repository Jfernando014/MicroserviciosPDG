package co.edu.unicauca.notification_microservice.service;

import co.edu.unicauca.notification_microservice.entity.AnteproyectoNotificacion;
import co.edu.unicauca.notification_microservice.infra.config.RabbitMQConfig;
import co.edu.unicauca.notification_microservice.repository.AnteproyectoNotificacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnteproyectoConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(AnteproyectoConsumerService.class);

    @Autowired
    private AnteproyectoNotificacionRepository repo;

    @RabbitListener(queues = RabbitMQConfig.ANTEPROYECTO_QUEUE)
    public void handle(AnteproyectoNotificacion notif) {
        repo.save(notif);

        StringBuilder msg = new StringBuilder("\n=== NOTIFICACIONES ANTEPROYECTO ===\n");

        if (notif.getJefeDepartamentoEmail() != null) {
            msg.append(String.format(
                "Para: %s | Asunto: Nuevo anteproyecto | Cuerpo: Revisar '%s' (ID: %d)\n",
                notif.getJefeDepartamentoEmail(), notif.getTitulo(), notif.getId()
            ));
        }
        if (notif.getEstudianteEmail() != null) {
            msg.append(String.format(
                "Para: %s | Asunto: Confirmación anteproyecto | Cuerpo: Registrado '%s'\n",
                notif.getEstudianteEmail(), notif.getTitulo()
            ));
        }
        if (notif.getTutor1Email() != null) {
            msg.append(String.format(
                "Para: %s | Asunto: Asignación como tutor | Cuerpo: Tutor de '%s'\n",
                notif.getTutor1Email(), notif.getTitulo()
            ));
        }
        if (notif.getTutor2Email() != null) {
            msg.append(String.format(
                "Para: %s | Asunto: Asignación como tutor | Cuerpo: Tutor de '%s'\n",
                notif.getTutor2Email(), notif.getTitulo()
            ));
        }

        logger.info(msg.toString());
    }
}