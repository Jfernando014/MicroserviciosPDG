package co.edu.unicauca.notification_microservice.service;

import co.edu.unicauca.notification_microservice.entity.FormatoANotificacion;
import co.edu.unicauca.notification_microservice.infra.config.RabbitMQConfig;
import co.edu.unicauca.notification_microservice.repository.FormatoANotificacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FormatoAConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(FormatoAConsumerService.class);

    @Autowired
    private FormatoANotificacionRepository repo;

    @RabbitListener(queues = RabbitMQConfig.FORMATO_A_QUEUE)
    public void handle(FormatoANotificacion notif) {
        repo.save(notif);
        logger.info("\n=== NOTIFICACIÓN FORMATO A ===\n" +
                "Para: {}\n" +
                "Asunto: Nuevo Formato A recibido\n" +
                "Proyecto: {} (ID: {})\n" +
                "Mensaje: Por favor revise el Formato A del proyecto.\n",
                notif.getCoordinadorEmail(), notif.getTitulo(), notif.getIdProyecto());
    }
}