package socialspring.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socialspring.exception.EmailAlreadyTakenException;
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

    private String generateUserName(String name) {
        long generatedNumber = (long) Math.floor(Math.random() * 1000000000);
        return name + generatedNumber;
    }
}
