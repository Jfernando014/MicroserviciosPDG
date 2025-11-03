package co.edu.unicauca.notificaciones.util;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange común
    public static final String EXCHANGE = "notificaciones.exchange";

    // Routing keys NUEVAS (proyecto las emite)
    public static final String RK_FORMATO_A_SUBIDO    = "formatoA.subido";
    public static final String RK_FORMATO_A_EVALUADO  = "formatoA.evaluado";
    public static final String RK_ANTEPROYECTO_SUBIDO = "anteproyecto.subido";

    // Colas NUEVAS (DTOs Event)
    public static final String Q_FORMATO_A_SUBIDO    = "formatoA.subido.q";
    public static final String Q_FORMATO_A_EVALUADO  = "formatoA.evaluado.q";
    public static final String Q_ANTEPROYECTO_SUBIDO = "anteproyecto.subido.q";

    // Colas LEGADO (entidades *Notificacion)
    public static final String FORMATO_A_QUEUE  = "formato-a.submitted";
    public static final String EVALUACION_QUEUE = "proyecto.evaluado";
    public static final String ANTEPROYECTO_QUEUE = "anteproyecto.submitted";

    // (si aún usas RK legado, mantenlas; si no, se quedarán inactivas)
    public static final String LEGACY_RK_FORMATO_A  = "formato-a.submitted";
    public static final String LEGACY_RK_EVALUACION = "proyecto.evaluado";
    public static final String LEGACY_RK_ANTEPROYECTO = "anteproyecto.submitted";

    @Bean
    public TopicExchange notificacionesExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    // Nuevas
    @Bean public Queue formatoASubidoQ()    { return new Queue(Q_FORMATO_A_SUBIDO, true); }
    @Bean public Queue formatoAEvaluadoQ()  { return new Queue(Q_FORMATO_A_EVALUADO, true); }
    @Bean public Queue anteproyectoSubidoQ(){ return new Queue(Q_ANTEPROYECTO_SUBIDO, true); }

    @Bean
    public Binding bindFormatoASubido(Queue formatoASubidoQ, TopicExchange notificacionesExchange) {
        return BindingBuilder.bind(formatoASubidoQ).to(notificacionesExchange).with(RK_FORMATO_A_SUBIDO);
    }
    @Bean
    public Binding bindFormatoAEvaluado(Queue formatoAEvaluadoQ, TopicExchange notificacionesExchange) {
        return BindingBuilder.bind(formatoAEvaluadoQ).to(notificacionesExchange).with(RK_FORMATO_A_EVALUADO);
    }
    @Bean
    public Binding bindAnteproyectoSubido(Queue anteproyectoSubidoQ, TopicExchange notificacionesExchange) {
        return BindingBuilder.bind(anteproyectoSubidoQ).to(notificacionesExchange).with(RK_ANTEPROYECTO_SUBIDO);
    }

    // Legado (opcional, si aún llega tráfico con estas RK)
    @Bean public Queue formatoALegacyQ()     { return new Queue(FORMATO_A_QUEUE, true); }
    @Bean public Queue evaluacionLegacyQ()   { return new Queue(EVALUACION_QUEUE, true); }
    @Bean public Queue anteproyectoLegacyQ() { return new Queue(ANTEPROYECTO_QUEUE, true); }

    @Bean
    public Binding bindFormatoALegacy(Queue formatoALegacyQ, TopicExchange notificacionesExchange) {
        return BindingBuilder.bind(formatoALegacyQ).to(notificacionesExchange).with(LEGACY_RK_FORMATO_A);
    }
    @Bean
    public Binding bindEvaluacionLegacy(Queue evaluacionLegacyQ, TopicExchange notificacionesExchange) {
        return BindingBuilder.bind(evaluacionLegacyQ).to(notificacionesExchange).with(LEGACY_RK_EVALUACION);
    }
    @Bean
    public Binding bindAnteproyectoLegacy(Queue anteproyectoLegacyQ, TopicExchange notificacionesExchange) {
        return BindingBuilder.bind(anteproyectoLegacyQ).to(notificacionesExchange).with(LEGACY_RK_ANTEPROYECTO);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
