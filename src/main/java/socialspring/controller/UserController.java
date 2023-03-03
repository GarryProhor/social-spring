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
        String username = tokenService.getUserNameFromToken(token);

        return userService.getUserByUsername(username);
    }

    @PostMapping("/pfp")
    public ApplicationUser uploadProfilePicture(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestParam("image")MultipartFile file) throws UnableToSavePhotoException {
        String username = tokenService.getUserNameFromToken(token);

        return userService.setProfileOrBannerPicture(username, file, "pfp");
    }

    @PostMapping("/banner")
    public ApplicationUser uploadBannerPicture(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestParam("image")MultipartFile file) throws UnableToSavePhotoException{
        String username = tokenService.getUserNameFromToken(token);
        return userService.setProfileOrBannerPicture(username, file, "bnr");
    }
}
