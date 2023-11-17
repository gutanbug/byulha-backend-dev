package Byulha.project.infra.s3.service;

import Byulha.project.global.auth.jwt.AppAuthentication;
import Byulha.project.infra.s3.ImageFileRepository;
import Byulha.project.infra.s3.exception.FileUploadFailedException;
import Byulha.project.infra.s3.model.ImageFile;
import Byulha.project.user.exception.UserNotFoundException;
import Byulha.project.user.model.entity.User;
import Byulha.project.user.repository.UserRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AmazonS3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    private final ImageFileRepository imageFileRepository;

    private final UserRepository userRepository;

    public List<String> uploadFile(AppAuthentication auth, List<MultipartFile> file) {
        User user = userRepository.findById(auth.getUserId()).orElseThrow(UserNotFoundException::new);

        List<String> fileNameList = new ArrayList<>();

        file.forEach(f -> {
            String fileName = createFileName(f.getOriginalFilename());
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(f.getSize());
            objectMetadata.setContentType(f.getContentType());

            try(InputStream inputStream = f.getInputStream()) {
                amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
                ImageFile.ImageFileBuilder imageFile = ImageFile.builder()
                        .user(user)
                        .contentType(f.getContentType())
                        .fileName(f.getOriginalFilename());

                imageFileRepository.save(imageFile.build());
            } catch(IOException e) {
                throw new FileUploadFailedException();
            }
            fileNameList.add(fileName);
        });
        return fileNameList;
    }

    public void deleteFile(String fileName) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }

    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "파일 확장자가 없습니다.");
        }
    }


}
