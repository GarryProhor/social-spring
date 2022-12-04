package socialspring.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socialspring.exception.EmailAlreadyTakenException;
import socialspring.exception.UserDoesNotException;
import socialspring.model.ApplicationUser;
import socialspring.model.RegistrationObject;
import socialspring.model.Role;
import socialspring.repository.ApplicationUserRepository;
import socialspring.repository.RoleRepository;
import socialspring.service.UserService;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final ApplicationUserRepository userRepository;
    private final RoleRepository roleRepository;

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
        userRepository.save(user);
    }


    private String generateUserName(String name) {
        long generatedNumber = (long) Math.floor(Math.random() * 1000_000_000);
        return name + generatedNumber;
    }

    private Long generateVerificationNumber() {
        return (long) Math.floor(Math.random() * 100_000_000);

    }
}
