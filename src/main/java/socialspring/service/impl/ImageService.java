package socialspring.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import socialspring.exception.UnableToResolvePhotoException;
import socialspring.exception.UnableToSavePhotoException;
import socialspring.model.Image;
import socialspring.repository.ImageRepository;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

@Service
@Transactional
public class ImageService {

    private final ImageRepository imageRepository;

    private static final File DIRECTORY = new File("C:\\Users\\igorp\\IdeaProjects\\social-spring\\img");
    private static final String URL = "http://localhost:8080/images/";

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Image uploadImage(MultipartFile file, String prefix) throws UnableToSavePhotoException {
        try {
//            The content type from request looks something like this img/jpeg
            String extension = "." + file.getContentType().split("/")[1];

            File img = File.createTempFile(prefix, extension, DIRECTORY);

            file.transferTo(img);

            String imageURL = URL + img.getName();

            Image image = new Image(img.getName(), file.getContentType(), img.getPath(), imageURL);

            Image saved = imageRepository.save(image);

            return saved;
        }catch (IOException e){
            throw new UnableToSavePhotoException();
        }
    }
    public byte[] downloadImage(String filename) throws UnableToResolvePhotoException {
        try {
            Image image = imageRepository.findByImageName(filename).get();
            String filePath = image.getImagePath();
            byte[] imageBytes = Files.readAllBytes(new File(filePath).toPath());
            return imageBytes;
        }catch (IOException e){
            throw new UnableToResolvePhotoException();
        }
    }
    public String getImageType(String fileName){
        Image image = imageRepository.findByImageName(fileName).get();
        return image.getImageType();
    }
}
