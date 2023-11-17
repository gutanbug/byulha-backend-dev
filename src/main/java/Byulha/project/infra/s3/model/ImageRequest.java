package Byulha.project.infra.s3.model;

import Byulha.project.infra.s3.exception.InvalidImageContentTypeException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Getter
@RequiredArgsConstructor
public class ImageRequest {
    private final String originalImageName;
    private final MediaType contentType;
    private final InputStreamSupplier inStreamSupplier;

    public ImageRequest(MultipartFile image) {
        this.originalImageName = image.getOriginalFilename();
        this.inStreamSupplier = image::getInputStream;

        String fileMimeType = image.getContentType();
        if (fileMimeType == null) {
            this.contentType = MediaType.APPLICATION_OCTET_STREAM;
        } else {
            try {
                this.contentType = MediaType.parseMediaType(image.getContentType());
            } catch (InvalidMediaTypeException e) {
                throw new InvalidImageContentTypeException(e);
            }
        }
    }

    @FunctionalInterface
    public interface InputStreamSupplier {
        InputStream get() throws IOException;
    }
}
