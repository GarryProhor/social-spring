package socialspring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import socialspring.exception.EmailAlreadyTakenException;
import socialspring.model.ApplicationUser;
import socialspring.model.RegistrationObject;
import socialspring.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
    private final UserService userService;

    @ExceptionHandler({EmailAlreadyTakenException.class})
    public ResponseEntity<String> handlerEmailTaken(){
        return new ResponseEntity<>("The email you provided is already", HttpStatus.CONFLICT);
    }

    // go to http://localhost:8080/auth/register
    @PostMapping("/register")
    public ApplicationUser registerUser(@RequestBody RegistrationObject regObject){
        return userService.registerUser(regObject);
    }
}
