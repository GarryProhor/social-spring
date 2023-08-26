package socialspring.controller;

import com.google.common.net.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import socialspring.exception.FollowException;
import socialspring.exception.UnableToSavePhotoException;
import socialspring.model.ApplicationUser;
import socialspring.service.UserService;
import socialspring.service.impl.ImageService;
import socialspring.service.impl.TokenService;

import java.util.LinkedHashMap;
import java.util.Set;

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

    @PutMapping("/")
    public ApplicationUser updateUser(@RequestBody ApplicationUser user){
        return userService.updateUser(user);
    }

    @ExceptionHandler({FollowException.class})
    public ResponseEntity<String> handleFollowException(){
        return new ResponseEntity<String>("User cannot follow themselves", HttpStatus.FORBIDDEN);
    }
    @PutMapping("/follow")
    public Set<ApplicationUser> followUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody LinkedHashMap<String, String> body) throws FollowException {
        String loggedInUser = tokenService.getUserNameFromToken(token);
        String followedUser = body.get("followedUser");

        return userService.followUser(loggedInUser, followedUser);
    }

    @GetMapping("/following/{username}")
    public Set<ApplicationUser> getFollowingList(@PathVariable("username") String username){
        return userService.retrieveFollowingList(username);
    }

    @GetMapping("/followers/{username}")
    public Set<ApplicationUser> getFollowersList(@PathVariable("username") String username){
        return userService.retrieveFollowersList(username);
    }
}
