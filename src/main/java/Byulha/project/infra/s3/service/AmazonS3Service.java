package Byulha.project.infra.s3.service;

import Byulha.project.infra.s3.model.ImageRequest;
import Byulha.project.infra.s3.model.UploadedImage;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class AmazonS3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3Client amazonS3Client;

    public ArrayList<UploadedImage> uploadImages(List<ImageRequest> images) {
        ArrayList<UploadedImage> uploadedImages = new ArrayList<>();
        for (ImageRequest image: images) {
            uploadedImages.add(uploadImage(image));
        }
        return uploadedImages;
    }

    public UploadedImage uploadImage(ImageRequest image) {
        String originName = image.getOriginalImageName();
        if (originName == null) originName = "";

        String ext = originName.substring(originName.lastIndexOf(".") + 1);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(ext);
        metadata.setContentLength(image.getSize());

        String imageId;
        imageId = createFileName(originName, ext);
        return upload(image, imageId, metadata);
    }

    private UploadedImage upload(ImageRequest image, String objectName, ObjectMetadata metadata) {
        try {
            amazonS3Client.putObject(new PutObjectRequest(bucket + "/", objectName, image.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            return new UploadedImage(objectName, image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteFile(String fileName) {
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }

    private String createFileName(String fileName, String extension) {
        return makeObjName(fileName, UUID.randomUUID() + "." + extension);
    }

    private String makeObjName(String fileName, String id) {return fileName + "-" + id;
    }
}
