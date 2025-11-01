package co.edu.unicauca.user_microservice.utilities.exception;

public class InvalidUserDataException extends Exception {
    public InvalidUserDataException(String message) {
        super(message);
    }
}
