package co.edu.unicauca.user_microservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import co.edu.unicauca.user_microservice.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
}
