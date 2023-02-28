package socialspring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import socialspring.exception.UnableToResolvePhotoException;
import socialspring.exception.UnableToSavePhotoException;
import socialspring.service.impl.ImageService;

@RestController
@RequestMapping("/images")
@CrossOrigin("*")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @ExceptionHandler({UnableToSavePhotoException.class, UnableToResolvePhotoException.class})
    public ResponseEntity<String> handlePhotoExceptions(){
        return new ResponseEntity<String>("Unable to process the photo", HttpStatus.NOT_ACCEPTABLE);
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<byte []> downloadImage(@PathVariable String fileName) throws UnableToResolvePhotoException {
        byte[] imageBytes = imageService.downloadImage(fileName);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.valueOf(imageService.getImageType(fileName)))
                .body(imageBytes);
    }
}
