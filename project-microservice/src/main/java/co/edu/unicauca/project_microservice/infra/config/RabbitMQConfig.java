package co.edu.unicauca.project_microservice.infra.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String NOTIFICACIONES_EXCHANGE = "notificaciones.exchange";
    public static final String FORMATO_A_ROUTING_KEY = "formato-a.submitted";
    public static final String EVALUACION_ROUTING_KEY = "proyecto.evaluado";
    public static final String ANTEPROYECTO_ROUTING_KEY = "anteproyecto.submitted";

    @Bean public DirectExchange notificacionesExchange() {
        return new DirectExchange(NOTIFICACIONES_EXCHANGE);
    }

    @Bean public Queue formatoAQueue() { return new Queue("formato-a.submitted", true); }
    @Bean public Queue evaluacionQueue() { return new Queue("proyecto.evaluado", true); }
    @Bean public Queue anteproyectoQueue() { return new Queue("anteproyecto.submitted", true); }

    @Bean public Binding bindingFormatoA() {
        return BindingBuilder.bind(formatoAQueue()).to(notificacionesExchange()).with(FORMATO_A_ROUTING_KEY);
    }
    @Bean public Binding bindingEvaluacion() {
        return BindingBuilder.bind(evaluacionQueue()).to(notificacionesExchange()).with(EVALUACION_ROUTING_KEY);
    }
    @Bean public Binding bindingAnteproyecto() {
        return BindingBuilder.bind(anteproyectoQueue()).to(notificacionesExchange()).with(ANTEPROYECTO_ROUTING_KEY);
    }

    @Bean public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}