package socialspring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import socialspring.exception.EmailAlreadyTakenException;
import socialspring.exception.EmailFailedToSendException;
import socialspring.exception.IncorrectVerificationCodeException;
import socialspring.exception.UserDoesNotException;
import socialspring.model.ApplicationUser;
import socialspring.model.LoginResponse;
import socialspring.model.RegistrationObject;
import socialspring.service.UserService;
import socialspring.service.impl.TokenService;

import java.util.LinkedHashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthenticationController {
    private final UserService userService;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    @ExceptionHandler({EmailAlreadyTakenException.class})
    public ResponseEntity<String> handlerEmailTaken() {
        return new ResponseEntity<>("The email you provided is already", HttpStatus.CONFLICT);
    }

    //    POST localhost:8080/auth/register
//    {
//            "firstName" : "abed",
//            "lastName" : "abed",
//            "email" : "test@test.ru",
//            "dob": "2022-12-03"
//    }
    @PostMapping("/register")
    public ApplicationUser registerUser(@RequestBody RegistrationObject regObject) {
        return userService.registerUser(regObject);
    }

    @ExceptionHandler({UserDoesNotException.class})
    public ResponseEntity<String> handleUserDoesntExist() {
        return new ResponseEntity<>("The user you are looking for doesnt exist", HttpStatus.NOT_FOUND);
    }

//    PUT localhost:8080/auth/update/phone
//    {
//        "userName": "abedabed663910176",
//        "phone": "123456789"
//    }

    @PutMapping("/update/phone")
    public ApplicationUser updatePhoneNumber(@RequestBody LinkedHashMap<String, String> body) {
        String username = body.get("userName");
        String phone = body.get("phone");

        ApplicationUser user = userService.getUserByUsername(username);
        user.setPhone(phone);

        return userService.updateUser(user);
    }

    @ExceptionHandler({EmailFailedToSendException.class})
    public ResponseEntity<String> handleFailedEmail() {
        return new ResponseEntity<>("Email failed to send, try again in a moment", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //    POST localhost:8080/auth/email/code
//    {
//        "userName": "abedabed663910176"
//    }
    @PostMapping("/email/code")
    public ResponseEntity<String> createEmailVerification(@RequestBody LinkedHashMap<String, String> body) {
        userService.generateEmailVerification(body.get("userName"));
        return new ResponseEntity<>("Verification code generated email sent", HttpStatus.OK);
    }

    @ExceptionHandler({IncorrectVerificationCodeException.class})
    public ResponseEntity<String> handleVerificationCode(){
        return new ResponseEntity<>("The code provided does not match the users code", HttpStatus.CONFLICT);
    }
    //    POST localhost:8080/auth/email/verify
//    {
//        "userName": "abedabed663910176"
//        "code": "123456879"
//    }
    @PostMapping("/email/verify")
    public ApplicationUser verifyEmail(@RequestBody LinkedHashMap<String, String> body){
        Long code = Long.parseLong(body.get("code"));
        String userName = body.get("userName");

        return userService.verifyEmail(userName, code);
    }

    //    PUT localhost:8080/auth/update/password
//    {
//        "userName": "abedabed663910176"
//        "password": "password"
//    }
    @PutMapping("/update/password")
    public ApplicationUser updatePassword(@RequestBody LinkedHashMap<String, String> body){
        String userName = body.get("userName");
        String password = body.get("password");

        return userService.setPassword(userName, password);
    }
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LinkedHashMap<String, String> body){
        String userName = body.get("userName");
        String password = body.get("password");

        try{
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userName, password)
            );

            String token = tokenService.generateToken(auth);
            return new LoginResponse(userService.getUserByUsername(userName), token);
        }catch (AuthenticationException e){
            return new LoginResponse(null, "");
        }
    }

}
