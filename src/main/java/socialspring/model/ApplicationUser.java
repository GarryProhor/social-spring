package socialspring.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Entity
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
@Getter
@Setter
public class ApplicationUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    Long userId;

    @Column(name = "first_name")
    String firstName;

    @Column(name = "last_name")
    String lastName;

    @Column(unique = true)
    String email;

    String phone;

    @Column(name = "dob")
    Date dateOfBirth;

    @Column(unique = true)
    String userName;

    @JsonIgnore
    String password;

    String bio;

    String nickname;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="profile_picture", referencedColumnName = "image_id")
    Image profilePicture;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="banner_picture", referencedColumnName = "image_id")
    Image bannerPicture;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name="following",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "following_id")}
    )
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    Set<ApplicationUser> following;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name="followers",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "followers_id")}
    )
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    Set<ApplicationUser> followers;

    /* Security related*/
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role_junction",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")}
    )
    Set<Role> authorities;



    Boolean enabled;

    @Column(nullable = true)
    @JsonIgnore
    Long verification;

    public ApplicationUser() {
        this.authorities = new HashSet<>();
        this.enabled = false;
    }
}
