package socialspring;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import socialspring.config.RSAKeyProperties;
import socialspring.model.Role;
import socialspring.repository.RoleRepository;
import socialspring.service.UserService;

@EnableConfigurationProperties(RSAKeyProperties.class)
@SpringBootApplication
public class SocialSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialSpringApplication.class, args);
    }

    @Bean
    CommandLineRunner run(RoleRepository roleRepository, UserService userService) {
        return args -> {
            roleRepository.save(new Role(1L, "USER"));
//            ApplicationUser user = new ApplicationUser();
//            user.setFirstName("garry");
//            user.setLastName("prohor");
//            userService.registerUser(user);
        };
    }
}
