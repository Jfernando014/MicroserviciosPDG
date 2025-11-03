package co.edu.unicauca.proyectos.services;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificacionesClient {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.routing.formatoA}")
    private String rkFormatoA;

    @Value("${app.rabbitmq.routing.evaluado}")
    private String rkEvaluado;

    @Value("${app.rabbitmq.routing.anteproyecto}")
    private String rkAnteproyecto;

    public void publicarFormatoASubido(Object evento) {
        rabbitTemplate.convertAndSend(exchange, rkFormatoA, evento);
    }

    public void publicarFormatoAEvaluado(Object evento) {
        rabbitTemplate.convertAndSend(exchange, rkEvaluado, evento);
    }

    public void publicarAnteproyectoSubido(Object evento) {
        rabbitTemplate.convertAndSend(exchange, rkAnteproyecto, evento);
    }
}