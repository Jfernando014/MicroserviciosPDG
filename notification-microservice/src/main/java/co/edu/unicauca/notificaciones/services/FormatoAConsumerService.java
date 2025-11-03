package co.edu.unicauca.notificaciones.services;

import co.edu.unicauca.notificaciones.models.FormatoANotificacion;
import co.edu.unicauca.notificaciones.util.RabbitMQConfig;
import co.edu.unicauca.notificaciones.repository.FormatoANotificacionRepository;
import co.edu.unicauca.notificaciones.dto.FormatoASubidoEvent;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j @Service
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

    @RabbitListener(queues = "formatoA.subido.q")
    public void onMessage(FormatoASubidoEvent e){
        log.info("Correo a {} | Formato A subido | id={} titulo={}",
                e.getCoordinadorEmail(), e.getIdProyecto(), e.getTitulo());
    }
}