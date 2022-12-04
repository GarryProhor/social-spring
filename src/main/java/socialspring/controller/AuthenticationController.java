package socialspring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import socialspring.exception.EmailAlreadyTakenException;
import socialspring.exception.EmailFailedToSendException;
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
}
