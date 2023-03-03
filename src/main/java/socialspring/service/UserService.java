package socialspring.service;

import org.springframework.web.multipart.MultipartFile;
import socialspring.exception.UnableToSavePhotoException;
import socialspring.model.ApplicationUser;
import socialspring.model.RegistrationObject;

public interface UserService {
    ApplicationUser registerUser(RegistrationObject registrationObject);

    ApplicationUser getUserByUsername(String username);

    ApplicationUser updateUser(ApplicationUser user);

    void generateEmailVerification(String userName);

    ApplicationUser verifyEmail(String userName, Long code);
    ApplicationUser setPassword(String userName, String password);

    ApplicationUser setProfileOrBannerPicture(String username, MultipartFile file, String prefix) throws UnableToSavePhotoException;
}
