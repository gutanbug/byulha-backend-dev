package Byulha.project.domain.user.service;

import Byulha.project.domain.user.exception.ImageNotFoundException;
import Byulha.project.domain.user.exception.UserNotFoundException;
import Byulha.project.domain.user.exception.WrongPasswordException;
import Byulha.project.domain.user.model.dto.request.RequestDeleteImageDto;
import Byulha.project.domain.user.model.dto.response.ResponseLoginDto;
import Byulha.project.domain.user.model.dto.response.ResponseReissueDto;
import Byulha.project.domain.user.model.dto.response.ResponseUserInfoDto;
import Byulha.project.domain.user.repository.UserRepository;
import Byulha.project.global.auth.jwt.AuthenticationToken;
import Byulha.project.global.auth.jwt.JwtProvider;
import Byulha.project.domain.user.model.dto.AutoLoginDto;
import Byulha.project.domain.user.model.dto.request.RequestLoginDto;
import Byulha.project.domain.user.model.entity.User;
import Byulha.project.domain.user.repository.AutoLoginRepository;
import Byulha.project.infra.s3.ImageFileRepository;
import Byulha.project.infra.s3.model.ImageFile;
import Byulha.project.infra.s3.model.ImageRequest;
import Byulha.project.infra.s3.model.UploadedImage;
import Byulha.project.infra.s3.model.dto.request.RequestUploadFileDto;
import Byulha.project.infra.s3.service.AmazonS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {

    public static final String AUTO_LOGIN_NAME = "auto-login";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AutoLoginRepository autoLoginRepository;
    private final ImageFileRepository imageFileRepository;
    private final AmazonS3Service amazonS3Service;

    public ResponseLoginDto login(RequestLoginDto dto) {
        Instant now = Instant.now();
        User user = userRepository.findByNickname(dto.getNickname())
                .orElseThrow(UserNotFoundException::new);

        if (passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            AuthenticationToken token = jwtProvider.issue(user);
            autoLoginRepository.setAutoLoginPayload(token.getRefreshToken(), AUTO_LOGIN_NAME,
                    new AutoLoginDto(user.getId().toString(), user.getUserRole()), now);
            return new ResponseLoginDto(token, user);
        } else {
            throw new WrongPasswordException();
        }
    }

    public ResponseReissueDto reissue(HttpServletRequest request, String refreshToken) {
        String accessToken = jwtProvider.getAccessTokenFromHeader(request);
        AuthenticationToken token = jwtProvider.reissue(accessToken, refreshToken);
        return new ResponseReissueDto(token);
    }

    public ResponseUserInfoDto getUserInfo(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        return new ResponseUserInfoDto(user.getName(), user.getNickname(),
                user.getPhone(), user.getAge(), user.getSex(), user.getUserRole().isAdmin());
    }

    @Transactional
    public void uploadImage(Long userId, RequestUploadFileDto dto) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        List<UploadedImage> uploadedImages = amazonS3Service.uploadImages(ImageRequest.ofList(dto.getImages()));
        List<ImageFile> imageFiles = new ArrayList<>();

        for (UploadedImage image: uploadedImages) {
            ImageFile.ImageFileBuilder builder = ImageFile.builder()
                    .fileName(image.getOriginalName())
                    .contentType(image.getMimeType().toString())
                    .fileId(image.getFileId())
                    .user(user);

            imageFiles.add(builder.build());
        }
        imageFileRepository.saveAll(imageFiles);
    }

    @Transactional
    public void deleteImage(Long userId, RequestDeleteImageDto dto) {
        ImageFile imageFile = imageFileRepository.findImageFileWithUserId(dto.getImageName(), userId).orElseThrow(ImageNotFoundException::new);
        amazonS3Service.deleteFile(imageFile.getFileId());
        imageFileRepository.delete(imageFile);
    }
}
