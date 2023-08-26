package socialspring.service;

import org.springframework.web.multipart.MultipartFile;
import socialspring.dto.FindUsernameDTO;
import socialspring.exception.FollowException;
import socialspring.exception.UnableToSavePhotoException;
import socialspring.model.ApplicationUser;
import socialspring.model.RegistrationObject;

import java.util.Set;

public interface UserService {
    ApplicationUser registerUser(RegistrationObject registrationObject);

    ApplicationUser getUserByUsername(String username);

    ApplicationUser updateUser(ApplicationUser user);

    void generateEmailVerification(String userName);

    ApplicationUser verifyEmail(String userName, Long code);
    ApplicationUser setPassword(String userName, String password);

    ApplicationUser setProfileOrBannerPicture(String username, MultipartFile file, String prefix) throws UnableToSavePhotoException;

    Set<ApplicationUser> followUser(String user, String followee) throws FollowException;

    Set<ApplicationUser> retrieveFollowersList(String username);

    Set<ApplicationUser> retrieveFollowingList(String username);

    String verifyUsername(FindUsernameDTO credential);

    ApplicationUser getUsersEmailAndPhone(FindUsernameDTO credential);
}
