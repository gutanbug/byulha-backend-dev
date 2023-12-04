package Byulha.project.domain.user.service;

import Byulha.project.domain.django.model.dto.request.RequestSendToDjangoDto;
import Byulha.project.domain.django.service.DjangoService;
import Byulha.project.domain.perfume.model.dto.response.ResponsePerfumeListDto;
import Byulha.project.domain.perfume.model.entity.Perfume;
import Byulha.project.domain.perfume.model.entity.PerfumeCategory;
import Byulha.project.domain.perfume.repository.PerfumeCategoryRepository;
import Byulha.project.domain.perfume.repository.PerfumeRepository;
import Byulha.project.domain.user.exception.ImageNotFoundException;
import Byulha.project.domain.user.exception.PerfumeCategoryNotFoundException;
import Byulha.project.domain.user.exception.UserNotFoundException;
import Byulha.project.domain.user.exception.WrongPasswordException;
import Byulha.project.domain.user.model.dto.request.RequestDeleteImageDto;
import Byulha.project.domain.user.model.dto.response.ResponseLoginDto;
import Byulha.project.domain.user.model.dto.response.ResponseReissueDto;
import Byulha.project.domain.user.model.dto.response.ResponseUserInfoDto;
import Byulha.project.domain.user.model.entity.ImageResult;
import Byulha.project.domain.user.repository.ImageResultRepository;
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
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {

    public static final String AUTO_LOGIN_NAME = "auto-login";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final MessageSource messageSource;
    private final AutoLoginRepository autoLoginRepository;
    private final ImageFileRepository imageFileRepository;
    private final PerfumeRepository perfumeRepository;
    private final PerfumeCategoryRepository perfumeCategoryRepository;
    private final ImageResultRepository imageResultRepository;
    private final AmazonS3Service amazonS3Service;
    private final DjangoService djangoService;

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
                user.getPhone(), user.getAge(), user.getGender(), user.getUserRole().isAdmin());
    }

    @Transactional
    public Page<ResponsePerfumeListDto> uploadImage(Long userId, RequestUploadFileDto dto, Pageable pageable) throws Exception{
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        List<String> fileIdList = new ArrayList<>();
        List<UploadedImage> uploadedImages = amazonS3Service.uploadImages(ImageRequest.ofList(dto.getImages()));
        List<ImageFile> imageFiles = new ArrayList<>();

        for (UploadedImage image: uploadedImages) {
            ImageFile.ImageFileBuilder builder = ImageFile.builder()
                    .fileName(image.getOriginalName())
                    .contentType(image.getMimeType().toString())
                    .fileId(image.getFileId())
                    .user(user);

            fileIdList.add(image.getFileId());
            imageFiles.add(builder.build());
        }
        imageFileRepository.saveAll(imageFiles);

        RequestSendToDjangoDto requestDto = RequestSendToDjangoDto.builder()
                .nickname(user.getNickname())
                .fileId(fileIdList.get(0))
                .build();

        List<Map.Entry<String, String>> result = djangoService.sendToDjango(requestDto);

        StringBuilder categoryName = new StringBuilder();
        for (Map.Entry<String, String> entry : result) {
            if(entry.getKey().equals("category_name")) {
                categoryName.append(entry.getValue());
            }
        }

        PerfumeCategory category = perfumeCategoryRepository.findByCategoryName(categoryName.toString())
                .orElseThrow(PerfumeCategoryNotFoundException::new);

        ImageResult imageResult = ImageResult.builder()
                .user(user)
                .fileId(fileIdList.get(0))
                .perfumeCategory(category)
                .build();
        imageResultRepository.save(imageResult);

        String[] split = category.getNotes().split(",");

        Page<Perfume> perfumeResult = perfumeRepository.findAllWithNotesOrderByLength(split[0], split[1],
                split[2], split[3], split[4], split[5], split[6], split[7], pageable);
        return perfumeResult.map(perfume -> new ResponsePerfumeListDto(perfume, messageSource));
    }

    @Transactional
    public void deleteImage(Long userId, RequestDeleteImageDto dto) {
        ImageFile imageFile = imageFileRepository.findImageFileWithUserId(dto.getImageName(), userId).orElseThrow(ImageNotFoundException::new);
        amazonS3Service.deleteFile(imageFile.getFileId());
        imageFileRepository.delete(imageFile);
    }
}
