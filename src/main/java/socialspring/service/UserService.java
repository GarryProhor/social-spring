package socialspring.service;

import socialspring.model.ApplicationUser;
import socialspring.model.RegistrationObject;

public interface UserService {
    ApplicationUser registerUser(RegistrationObject registrationObject);

    ApplicationUser getUserByUsername(String username);

    ApplicationUser updateUser(ApplicationUser user);

    void generateEmailVerification(String userName);
}
