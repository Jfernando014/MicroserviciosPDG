package co.edu.unicauca.notification_microservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class EvaluacionNotificacion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long idProyecto;
    private boolean aprobado;
    private String observaciones;
    private String[] destinatarios;
}
