package co.edu.unicauca.messaging_microservice.repository;

import co.edu.unicauca.messaging_microservice.entity.MensajeInterno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeInternoRepository extends JpaRepository<MensajeInterno, Long> {
    List<MensajeInterno> findByRemitenteEmail(String remitenteEmail);
    List<MensajeInterno> findByDestinatariosEmailContaining(String destinatarioEmail);
}