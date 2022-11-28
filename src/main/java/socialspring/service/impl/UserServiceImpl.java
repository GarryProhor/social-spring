package socialspring.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socialspring.model.ApplicationUser;
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
    public ApplicationUser registerUser(ApplicationUser user) {
        Set<Role> roles = user.getAuthorities();
        roles.add(roleRepository.findByAuthority("USER").get());
        user.setAuthorities(roles);
        return userRepository.save(user);
    }
}
