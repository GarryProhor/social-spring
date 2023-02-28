package socialspring.controller;

import com.google.common.net.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import socialspring.exception.UnableToSavePhotoException;
import socialspring.model.ApplicationUser;
import socialspring.service.UserService;
import socialspring.service.impl.ImageService;
import socialspring.service.impl.TokenService;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {

    private final UserService userService;
    private final TokenService tokenService;
    private final ImageService imageService;

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

    @PostMapping("/pfp")
    public ResponseEntity<String> uploadProfilePicture(@RequestParam("image")MultipartFile file) throws UnableToSavePhotoException {
        String uploadImage = imageService.uploadImage(file, "pfp");
        return ResponseEntity.status(HttpStatus.OK).body(uploadImage);

    }
}
