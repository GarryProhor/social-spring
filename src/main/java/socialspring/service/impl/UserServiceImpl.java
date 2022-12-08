package socialspring.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import socialspring.exception.EmailAlreadyTakenException;
import socialspring.exception.EmailFailedToSendException;
import socialspring.exception.IncorrectVerificationCodeException;
import socialspring.exception.UserDoesNotException;
import socialspring.model.ApplicationUser;
import socialspring.model.RegistrationObject;
import socialspring.model.Role;
import socialspring.repository.ApplicationUserRepository;
import socialspring.repository.RoleRepository;
import socialspring.service.MailService;
import socialspring.service.UserService;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final ApplicationUserRepository userRepository;
    private final RoleRepository roleRepository;

    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ApplicationUser registerUser(RegistrationObject ro) {
        ApplicationUser user = new ApplicationUser();
        user.setFirstName(ro.getFirstName());
        user.setLastName(ro.getLastName());
        user.setEmail(ro.getEmail());
        user.setDateOfBirth(ro.getDob());

        String name = user.getFirstName() + user.getLastName();

        boolean nameTaken = true;
        String tempName = "";

        while (nameTaken){
            tempName = generateUserName(name);
            if(userRepository.findByUserName(tempName).isEmpty()){
                nameTaken = false;
            }
        }
        user.setUserName(tempName);

        Set<Role> roles = user.getAuthorities();
        roles.add(roleRepository.findByAuthority("USER").get());
        user.setAuthorities(roles);

        try{
            return userRepository.save(user);
        }catch (Exception e){
            throw new EmailAlreadyTakenException();
        }
    }

    @Override
    public ApplicationUser getUserByUsername(String username) {
        return userRepository.findByUserName(username).orElseThrow(UserDoesNotException::new);
    }

    @Override
    public ApplicationUser updateUser(ApplicationUser user) {
        try{
            return userRepository.save(user);
        }catch (Exception e){
            throw new EmailAlreadyTakenException();
        }
    }

    @Override
    public void generateEmailVerification(String userName) {
        ApplicationUser user = userRepository.findByUserName(userName).orElseThrow(UserDoesNotException::new);
        
        user.setVerification(generateVerificationNumber());

        try {
            mailService.sendEmail(user.getEmail(),
                    "Your verification code",
                    "Here is your verification code " + user.getVerification());
            userRepository.save(user);
        } catch (Exception e) {
            throw new EmailFailedToSendException();
        }
        userRepository.save(user);
    }

    @Override
    public ApplicationUser verifyEmail(String userName, Long code) {
        ApplicationUser user = userRepository.findByUserName(userName).orElseThrow(UserDoesNotException::new);

        if (code.equals(user.getVerification())){
            user.setEnabled(true);
            user.setVerification(null);
            return userRepository.save(user);
        }else {
            throw new IncorrectVerificationCodeException();
        }
    }

    @Override
    public ApplicationUser setPassword(String userName, String password) {
        ApplicationUser user = userRepository.findByUserName(userName).orElseThrow(UserDoesNotException::new);

        String encoderPassword = passwordEncoder.encode(password);
        user.setPassword(encoderPassword);

        return userRepository.save(user);
    }


    private String generateUserName(String name) {
        long generatedNumber = (long) Math.floor(Math.random() * 1000_000_000);
        return name + generatedNumber;
    }

    private Long generateVerificationNumber() {
        return (long) Math.floor(Math.random() * 100_000_000);

    }
}
