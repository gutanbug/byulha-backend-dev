package Byulha.project.infra.s3.controller;

import Byulha.project.global.auth.jwt.AppAuthentication;
import Byulha.project.global.auth.role.UserAuth;
import Byulha.project.infra.s3.model.dto.request.RequestUploadFileDto;
import Byulha.project.infra.s3.model.dto.response.ResponseUploadFileDto;
import Byulha.project.infra.s3.service.AmazonS3Service;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "AWS S3", description = "AWS S3 관련 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/s3")
public class AmazonS3Controller {

    private final AmazonS3Service amazonS3Service;

    /**
     * AWS S3에 이미지 파일 업로드
     *
     * @param dto 이미지 파일 목록 dto
     */
    @UserAuth
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseUploadFileDto uploadFile(AppAuthentication auth, @Valid @ModelAttribute RequestUploadFileDto dto) {
        List<String> imageList = amazonS3Service.uploadFile(auth, dto.getFiles());
        return new ResponseUploadFileDto(imageList);
    }
}
