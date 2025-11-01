package co.edu.unicauca.project_microservice.repository;

import co.edu.unicauca.project_microservice.entity.ProyectoGrado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<ProyectoGrado, Long> {
    List<ProyectoGrado> findByEstudiante1Email(String email);
    List<ProyectoGrado> findByDirectorEmail(String email);
}