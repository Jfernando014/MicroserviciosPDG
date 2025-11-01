package co.edu.unicauca.user_microservice.service;

import co.edu.unicauca.user_microservice.entity.Usuario;
import co.edu.unicauca.user_microservice.utilities.exception.InvalidUserDataException;
import co.edu.unicauca.user_microservice.utilities.exception.UserAlreadyExistsException;

public interface IUsuarioService {
    Usuario registrarDocente(Usuario usuario) throws UserAlreadyExistsException, InvalidUserDataException;
    Usuario registrarEstudiante(Usuario usuario) throws UserAlreadyExistsException, InvalidUserDataException;
    Usuario registrarCoordinador(Usuario usuario) throws UserAlreadyExistsException, InvalidUserDataException;
    Usuario registrarJefeDepartamento(Usuario usuario) throws UserAlreadyExistsException, InvalidUserDataException;
    Usuario obtenerPorEmail(String email) throws InvalidUserDataException;
    boolean existeUsuario(String email);
    String obtenerRol(String email);
}