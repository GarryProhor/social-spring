package socialspring.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import socialspring.exception.*;
import socialspring.model.ApplicationUser;
import socialspring.model.Image;
import socialspring.model.RegistrationObject;
import socialspring.model.Role;
import socialspring.repository.ApplicationUserRepository;
import socialspring.repository.RoleRepository;
import socialspring.service.MailService;
import socialspring.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {
    private final ApplicationUserRepository userRepository;
    private final RoleRepository roleRepository;

    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    private final ImageService imageService;

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

    @Override
    public ApplicationUser setProfileOrBannerPicture(String username, MultipartFile file, String prefix) throws UnableToSavePhotoException {
       ApplicationUser user = userRepository.findByUserName(username).orElseThrow(UserDoesNotException::new);

        Image photo = imageService.uploadImage(file, prefix);

        try {
            if(prefix.equals("pfp")){
                if(user.getProfilePicture() != null && !user.getProfilePicture().getImageName().equals("defaultpfp.png")){
                    Path p = Paths.get(user.getProfilePicture().getImagePath());
                    Files.deleteIfExists(p);
                }
                user.setProfilePicture(photo);
            }else {
                if(user.getBannerPicture() != null && !user.getBannerPicture().getImageName().equals("defaultbnr.png")){
                    Path p = Paths.get(user.getBannerPicture().getImagePath());
                    Files.deleteIfExists(p);
                }
                user.setBannerPicture(photo);
            }
        }catch (IOException e){
            throw new UnableToSavePhotoException();
        }

       return userRepository.save(user);
    }


    private String generateUserName(String name) {
        long generatedNumber = (long) Math.floor(Math.random() * 1000_000_000);
        return name + generatedNumber;
    }

    private Long generateVerificationNumber() {
        return (long) Math.floor(Math.random() * 100_000_000);

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ApplicationUser u = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Set<GrantedAuthority> authorities = u.getAuthorities()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toSet());

        UserDetails ud = new User(u.getUserName(), u.getPassword(), authorities);
        return ud;
    }
    @Override
    public Set<ApplicationUser> followUser(String user, String followee) throws FollowException{

        if(user.equals(followee)) throw new FollowException();

        ApplicationUser loggedInUser = userRepository.findByUserName(user).orElseThrow(UserDoesNotException::new);

        Set<ApplicationUser> followingList = loggedInUser.getFollowing();

        ApplicationUser followedUser = userRepository.findByUserName(followee).orElseThrow(UserDoesNotException::new);

        Set<ApplicationUser> followersList = followedUser.getFollowers();

        //Add the followed use to the following list
        followingList.add(followedUser);
        loggedInUser.setFollowing(followingList);

        //Add the current user to the follower list of the followee
        followersList.add(loggedInUser);
        followedUser.setFollowers(followersList);

        //Update both users
        userRepository.save(loggedInUser);
        userRepository.save(followedUser);

        return  loggedInUser.getFollowing();
    }

    @Override
    public Set<ApplicationUser> retrieveFollowersList(String username) {
        ApplicationUser user = userRepository.findByUserName(username).orElseThrow(UserDoesNotException::new);

        return user.getFollowers();
    }

    @Override
    public Set<ApplicationUser> retrieveFollowingList(String username) {
        ApplicationUser user = userRepository.findByUserName(username).orElseThrow(UserDoesNotException::new);

        return user.getFollowing();
    }

}
