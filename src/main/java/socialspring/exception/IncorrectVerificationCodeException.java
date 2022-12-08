package socialspring.exception;

import java.io.Serial;

public class IncorrectVerificationCodeException extends RuntimeException{
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 1L;

    public IncorrectVerificationCodeException(){
        super("The code passed did not match the users verification code");
    }
}
