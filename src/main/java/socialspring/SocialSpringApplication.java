package socialspring;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import socialspring.config.RSAKeyProperties;
import socialspring.model.ApplicationUser;
import socialspring.model.Role;
import socialspring.repository.RoleRepository;
import socialspring.repository.UserRepository;

import java.util.HashSet;
import java.util.Set;

@EnableConfigurationProperties(RSAKeyProperties.class)
@SpringBootApplication
public class SocialSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialSpringApplication.class, args);
    }

    @Bean
    CommandLineRunner run(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder encoder) {
        return args -> {
            Role r = roleRepository.save(new Role(1L, "USER"));

            Set<Role> roles = new HashSet<>();
            roles.add(r);

            ApplicationUser user = new ApplicationUser();
            user.setAuthorities(roles);
            user.setFirstName("garry");
            user.setLastName("prohor");
            user.setEmail("igorprohorchenko@gmail.com");
            user.setUserName("GarryProhor");
            user.setPassword(encoder.encode("password"));
            user.setEnabled(true);

            userRepository.save(user);
        };
    }
}
