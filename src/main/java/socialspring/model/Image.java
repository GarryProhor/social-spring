package socialspring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Data
@Entity
@Table(name = "images")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "image_id", unique = true)
    Long imageId;

    @Column(name = "image_name")
    String imageName;

    @Column(name = "image_type")
    String imageType;

    @Column(name = "image_path")
    @JsonIgnore
    String imagePath;

    @Column(name = "image_url")
    String imageURL;

    public Image() {
    }

    public Image(Long imageId, String imageName, String imageType, String imagePath, String imageURL) {
        this.imageId = imageId;
        this.imageName = imageName;
        this.imageType = imageType;
        this.imagePath = imagePath;
        this.imageURL = imageURL;
    }

    public Image(String imageName, String imageType, String imagePath, String imageURL) {
        this.imageName = imageName;
        this.imageType = imageType;
        this.imagePath = imagePath;
        this.imageURL = imageURL;
    }
}
