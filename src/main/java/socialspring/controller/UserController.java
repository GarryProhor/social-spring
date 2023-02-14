package socialspring.controller;

import com.google.common.net.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import socialspring.model.ApplicationUser;
import socialspring.service.UserService;
import socialspring.service.impl.TokenService;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {

    private final UserService userService;
    private final TokenService tokenService;

    @GetMapping("/verify")
    public ApplicationUser verifyIdentity(@RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        String userName = "";
        ApplicationUser user;

        if(token.substring(0,6).equals("Bearer")){
            String strippedToken = token.substring(7);
            userName = tokenService.getUserNameFromToken(strippedToken);
        }
        try {
            user = userService.getUserByUsername(userName);
        }catch (Exception e){
            user = null;
        }
        return user;
    }
}
