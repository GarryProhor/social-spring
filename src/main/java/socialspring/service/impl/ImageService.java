package socialspring.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import socialspring.model.Image;
import socialspring.repository.ImageRepository;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;

@Service
@Transactional
public class ImageService {

    private final ImageRepository imageRepository;

    private static final File DIRECTORY = new File("C:\\Users\\igorp\\IdeaProjects\\social-spring\\img");
    private static final String URL = "http://localhost:8080/images/";

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public String uploadImage(MultipartFile file, String prefix){
        try {
//            The content type from request looks something like this img/jpeg
            String extension = "." + file.getContentType().split("/")[1];

            File img = File.createTempFile(prefix, extension, DIRECTORY);

            file.transferTo(img);

            String imageURL = URL + img.getName();

            Image image = new Image(img.getName(), file.getContentType(), img.getPath(), imageURL);

            Image saved = imageRepository.save(image);

            return "file uploaded successfully: " + img.getName();
        }catch (IOException e){
            e.printStackTrace();
            return "file uploaded unsuccessfully";
        }
    }
}
