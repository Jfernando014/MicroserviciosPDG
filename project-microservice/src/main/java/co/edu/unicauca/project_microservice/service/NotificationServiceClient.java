package co.edu.unicauca.project_microservice.service;

import co.edu.unicauca.project_microservice.infra.dto.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceClient implements INotificationServiceClient {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.routingkey.formato_a_subido}")
    private String routingKeyFormatoA;

    @Value("${app.rabbitmq.routingkey.evaluado}")
    private String routingKeyEvaluado;

    @Value("${app.rabbitmq.routingkey.anteproyecto}")
    private String routingKeyAnteproyecto;

    @Override
    public void notificarFormatoASubido(FormatoASubidoEvent event) {
        rabbitTemplate.convertAndSend(exchange, routingKeyFormatoA, event);
    }

    @Override
    public void notificarEvaluacion(ProyectoEvaluadoEvent event) {
        rabbitTemplate.convertAndSend(exchange, routingKeyEvaluado, event);
    }

    @Override
    public void notificarAnteproyectoSubido(AnteproyectoSubidoEvent event) {
        rabbitTemplate.convertAndSend(exchange, routingKeyAnteproyecto, event);
    }
}