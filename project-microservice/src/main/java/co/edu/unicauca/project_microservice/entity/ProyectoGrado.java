package co.edu.unicauca.project_microservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "proyectos")
@Data
@NoArgsConstructor
public class ProyectoGrado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String modalidad;
    private String directorEmail;
    private String codirectorEmail;
    private String estudiante1Email;
    private String estudiante2Email;
    private String objetivoGeneral;
    private String objetivosEspecificos;
    private String observacionesEvaluacion;
    private int numeroIntento = 1;
    private String estadoActual; // Persistente

    @Transient
    private EstadoProyecto estado;

    public void setEstado(EstadoProyecto estado) {
        this.estado = estado;
        if (estado != null) {
            this.estadoActual = estado.getNombreEstado();
        }
    }

    public String getEstadoActual() {
        return estadoActual != null ? estadoActual : "DESCONOCIDO";
    }

    public void evaluar(boolean aprobado, String observaciones) {
        if (estado == null) throw new IllegalStateException("Estado no inicializado.");
        estado.evaluar(this, aprobado, observaciones);
    }

    public void reintentar() {
        if (estado == null) throw new IllegalStateException("Estado no inicializado.");
        estado.reintentar(this);
    }
}