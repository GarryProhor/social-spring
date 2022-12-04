package socialspring.exception;

import java.io.Serial;

public class UserDoesNotException extends RuntimeException{

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 1L;

    public UserDoesNotException(){
        super("The user you are looking for does not exist");
    }


}
