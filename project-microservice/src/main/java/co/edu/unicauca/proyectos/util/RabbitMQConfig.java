package co.edu.unicauca.proyectos.util;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.host:localhost}")
    private String host;

    @Value("${spring.rabbitmq.port:5672}")
    private int port;

    @Value("${spring.rabbitmq.username:guest}")
    private String username;

    @Value("${spring.rabbitmq.password:guest}")
    private String password;

    @Value("${app.rabbitmq.exchange:notificaciones.exchange}")
    private String exchangeName;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory cf = new CachingConnectionFactory(host, port);
        cf.setUsername(username);
        cf.setPassword(password);
        return cf;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf) {
        return new RabbitTemplate(cf);
    }

    // Declaramos solo el Exchange (tipo topic) que ya existe en el broker.
    @Bean
    public TopicExchange notificacionesExchange() {
        return new TopicExchange(exchangeName, true, false);
    }
}
