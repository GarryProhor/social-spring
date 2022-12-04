package socialspring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import socialspring.exception.EmailAlreadyTakenException;
import socialspring.exception.UserDoesNotException;
import socialspring.model.ApplicationUser;
import socialspring.model.RegistrationObject;
import socialspring.service.UserService;

import java.util.LinkedHashMap;

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

    @ExceptionHandler({UserDoesNotException.class})
    public ResponseEntity<String> handleUserDoesntExist(){
        return new ResponseEntity<>("The user you are looking for doesnt exist", HttpStatus.NOT_FOUND);
    }

    @PutMapping("/update/phone")
    public ApplicationUser updatePhoneNumber(@RequestBody LinkedHashMap<String , String> body){
        String username = body.get("userName");
        String phone = body.get("phone");

        ApplicationUser user = userService.getUserByUsername(username);
        user.setPhone(phone);

        return userService.updateUser(user);
    }

    @PostMapping("/email/code")
    public ResponseEntity<String> createEmailVerification(@RequestBody LinkedHashMap<String, String> body){
        userService.generateEmailVerification(body.get("userName"));
        return new ResponseEntity<>("Verification code generated? email sent", HttpStatus.OK);
    }
}
