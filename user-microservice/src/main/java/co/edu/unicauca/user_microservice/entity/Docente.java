package co.edu.unicauca.user_microservice.entity;

import co.edu.unicauca.user_microservice.entity.enums.TipoDocente;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Docente extends Usuario {
    private TipoDocente tipoDocente;
}
