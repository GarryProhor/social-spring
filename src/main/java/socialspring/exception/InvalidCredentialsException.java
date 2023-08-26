package socialspring.exception;

public class InvalidCredentialsException extends Exception {
    public InvalidCredentialsException() {
        super("Username or password does not exist");
    }
}
