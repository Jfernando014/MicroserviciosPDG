package co.edu.unicauca.notificaciones.services;

import co.edu.unicauca.notificaciones.models.EvaluacionNotificacion;
import co.edu.unicauca.notificaciones.util.RabbitMQConfig;
import co.edu.unicauca.notificaciones.repository.EvaluacionNotificacionRepository;
import co.edu.unicauca.notificaciones.dto.EvaluacionFormatoAEvent;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j @Service
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

    @RabbitListener(queues = "formatoA.evaluado.q")
    public void onMessage(EvaluacionFormatoAEvent e){
        log.info("Evaluación Formato A | id={} aprobado={} obs='{}' | est(s)={},{} | dir={} co-dir={}",
                e.getIdProyecto(), e.isAprobado(), e.getObservaciones(),
                e.getEstudianteEmail1(), e.getEstudianteEmail2(),
                e.getDirectorEmail(), e.getCodirectorEmail());
    }

}