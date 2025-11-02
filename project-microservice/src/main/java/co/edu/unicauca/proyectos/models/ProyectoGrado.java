package co.edu.unicauca.proyectos.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import co.edu.unicauca.proyectos.models.estados.*;

@Entity @Table(name="proyectos")
@Data @NoArgsConstructor
public class ProyectoGrado {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String modalidad; // INVESTIGACION | PRACTICA_PROFESIONAL
    private String directorEmail;
    private String codirectorEmail;
    private String estudiante1Email;
    private String estudiante2Email;
    @Column(length=2000)
    private String objetivoGeneral;
    @Column(length=4000)
    private String objetivosEspecificos;

    private Integer intentos = 0;                     // 0..3
    private String estadoActual = "EN_PRIMERA_EVALUACION_FORMATO_A";
    @Transient
    private EstadoProyecto estado = new EnPrimeraEvaluacionState();

    @Column(length=2000)
    private String observacionesEvaluacion;

    private String formatoAToken;
    private String cartaToken;
    private String anteproyectoToken;

    // setters sincronizan nombre de estado visible
    public void setEstado(EstadoProyecto nuevo) {
        this.estado = nuevo;
        this.estadoActual = nuevo.getNombreEstado();
    }
    public String getEstadoActual() { return estadoActual; }

    public void evaluar(boolean aprobado, String observaciones) {
        if (estado == null) setEstado(fromNombre(estadoActual));
        this.estado.evaluar(this, aprobado, observaciones);
    }

    public void reintentar() {
        if (estado == null) setEstado(fromNombre(estadoActual));
        this.estado.reintentar(this);
    }

    // fábrica simple por nombre persistido
    private EstadoProyecto fromNombre(String nombre) {
        return switch (nombre) {
            case "EN_PRIMERA_EVALUACION_FORMATO_A" -> new EnPrimeraEvaluacionState();
            case "EN_SEGUNDA_EVALUACION_FORMATO_A" -> new EnSegundaEvaluacionState();
            case "EN_TERCERA_EVALUACION_FORMATO_A" -> new EnTerceraEvaluacionState();
            case "FORMATO_A_APROBADO" -> new FormatoAAprobadoState();
            case "RECHAZADO_DEFINITIVO" -> new RechazadoDefinitivoState();
            default -> new EnPrimeraEvaluacionState();
        };
    }

    // helpers usados por estados
    public void incrementarIntentoORechazarDefinitivo() {
        this.intentos = this.intentos == null ? 0 : this.intentos;
        if (this.intentos >= 2) {        // al 3er rechazo
            setEstado(new RechazadoDefinitivoState());
        } else {
            this.intentos += 1;
        }
    }

    // ProyectoGrado.java  (añade adaptadores)
    public Integer getNumeroIntento(){
        return this.intentos;
    }

    public void setNumeroIntento(int n){
        this.intentos = n;
    }

}
