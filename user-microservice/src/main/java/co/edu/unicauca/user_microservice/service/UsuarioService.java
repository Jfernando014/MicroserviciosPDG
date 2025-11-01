package co.edu.unicauca.user_microservice.service;

import co.edu.unicauca.user_microservice.entity.*;
import co.edu.unicauca.user_microservice.repository.UsuarioRepository;
import co.edu.unicauca.user_microservice.utilities.exception.InvalidUserDataException;
import co.edu.unicauca.user_microservice.utilities.exception.UserAlreadyExistsException;
import co.edu.unicauca.user_microservice.utilities.security.PasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService implements IUsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public Usuario registrarDocente(Usuario usuario) throws UserAlreadyExistsException, InvalidUserDataException {
        validarUsuario(usuario);
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario registrarEstudiante(Usuario usuario) throws UserAlreadyExistsException, InvalidUserDataException {
        validarUsuario(usuario);
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario registrarCoordinador(Usuario usuario) throws UserAlreadyExistsException, InvalidUserDataException {
        validarUsuario(usuario);
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario registrarJefeDepartamento(Usuario usuario) throws UserAlreadyExistsException, InvalidUserDataException {
        validarUsuario(usuario);
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario obtenerPorEmail(String email) throws InvalidUserDataException {
        return usuarioRepository.findById(email)
                .orElseThrow(() -> new InvalidUserDataException("Usuario no encontrado."));
    }

    @Override
    public boolean existeUsuario(String email) {
        return usuarioRepository.existsById(email);
    }

    @Override
    public String obtenerRol(String email) {
        return usuarioRepository.findById(email)
                .map(usuario -> {
                    if (usuario instanceof Docente) return "DOCENTE";
                    if (usuario instanceof Estudiante) return "ESTUDIANTE";
                    if (usuario instanceof Coordinador) return "COORDINADOR";
                    if (usuario instanceof JefeDepartamento) return "JEFE_DEPARTAMENTO";
                    return "DESCONOCIDO";
                })
                .orElse("DESCONOCIDO");
    }

    private void validarUsuario(Usuario usuario) throws InvalidUserDataException, UserAlreadyExistsException {
        if (usuario.getEmail() == null || !usuario.getEmail().endsWith("@unicauca.edu.co")) {
            throw new InvalidUserDataException("El email debe ser del dominio @unicauca.edu.co");
        }
        if (!PasswordValidator.isValid(usuario.getPassword())) {
            throw new InvalidUserDataException("La contraseña no cumple con los requisitos.");
        }
        if (usuarioRepository.existsById(usuario.getEmail())) {
            throw new UserAlreadyExistsException("El usuario ya existe.");
        }
    }
}