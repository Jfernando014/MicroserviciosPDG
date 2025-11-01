package co.edu.unicauca.notification_microservice.service;

import co.edu.unicauca.notification_microservice.entity.EvaluacionNotificacion;
import co.edu.unicauca.notification_microservice.infra.config.RabbitMQConfig;
import co.edu.unicauca.notification_microservice.repository.EvaluacionNotificacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EvaluacionConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(EvaluacionConsumerService.class);

    @Autowired
    private EvaluacionNotificacionRepository repo;

    @RabbitListener(queues = RabbitMQConfig.EVALUACION_QUEUE)
    public void handle(EvaluacionNotificacion notif) {
        repo.save(notif);
        String resultado = notif.isAprobado() ? "APROBADO" : "RECHAZADO";
        logger.info("\n=== NOTIFICACIÓN DE EVALUACIÓN ===\n" +
                "Para: {}\n" +
                "Asunto: Evaluación de Formato A - {}\n" +
                "Proyecto ID: {}\n" +
                "Observaciones: {}\n",
                String.join(", ", notif.getDestinatarios()),
                resultado,
                notif.getIdProyecto(),
                notif.getObservaciones() != null ? notif.getObservaciones() : "Ninguna");
    }
}