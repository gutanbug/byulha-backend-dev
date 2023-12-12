package Byulha.project.domain.user.service;

import Byulha.project.domain.django.model.dto.request.RequestSendToDjangoDto;
import Byulha.project.domain.django.service.DjangoService;
import Byulha.project.domain.perfume.model.dto.response.ResponsePerfumeAIListDto;
import Byulha.project.domain.perfume.model.dto.response.ResponsePerfumeListDto;
import Byulha.project.domain.perfume.model.entity.Perfume;
import Byulha.project.domain.perfume.model.entity.PerfumeCategory;
import Byulha.project.domain.perfume.repository.PerfumeCategoryRepository;
import Byulha.project.domain.perfume.repository.PerfumeRepository;
import Byulha.project.domain.perfume.service.PerfumeService;
import Byulha.project.domain.user.exception.ImageNotFoundException;
import Byulha.project.domain.user.exception.PerfumeCategoryNotFoundException;
import Byulha.project.domain.user.exception.UserNotFoundException;
import Byulha.project.domain.user.exception.WrongPasswordException;
import Byulha.project.domain.user.model.dto.request.RequestDeleteImageDto;
import Byulha.project.domain.user.model.dto.response.ResponseLoginDto;
import Byulha.project.domain.user.model.dto.response.ResponsePerfumeLastAIListDto;
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
import java.util.stream.Collectors;

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
    private final PerfumeService perfumeService;
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
    public ResponsePerfumeLastAIListDto uploadImage(Long userId, RequestUploadFileDto dto, Pageable pageable) throws Exception{

        //TODO : 이미지를 통해 분위기를 3개 받고나면 분위기1등에 해당하는 AI 생성 이미지를 매핑하여 이미지에 대한 설명과 같이 출력해주는 것을 추가해야함.
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

        List<Map.Entry<String, String>> result = djangoService.sendToDjangoForMood(requestDto);

        StringBuilder categoryPercent = new StringBuilder();
        for (Map.Entry<String, String> entry : result) {
            if(entry.getKey().equals("category_percent")) {
                categoryPercent.append(entry.getValue());
            }
        }

        Set<String> notes = perfumeService.getUniqueNotes();
        HashMap<String, Double> notesMap = new HashMap<>();
        for (String note: notes) {
            notesMap.put(note, 0.0);
        }

        String category = categoryPercent.toString();
        System.out.println(category);
        String[] category_split = category.split("-");
        for(String percentData : category_split){
            String[] note_percent = percentData.split(":");
            PerfumeCategory perfumeCategory = perfumeCategoryRepository.findByCategoryName(note_percent[0].toUpperCase())
                    .orElseThrow(PerfumeCategoryNotFoundException::new);
            String[] category_notes = perfumeCategory.getNotes().split(",");
            for(String note : category_notes) {
                for(String key: notesMap.keySet()) {
                    if(note.toLowerCase().equals(key)){
                        notesMap.put(key, notesMap.get(key) + Double.parseDouble(note_percent[1]));
                        System.out.println(key + " : " + notesMap.get(key));
                    }
                }
            }
        }

        Map<String, Double> sortedNotesMap = sortedMapDesc(notesMap);

        List<String> top3notes = sortedNotesMap.entrySet().stream()
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        System.out.println(top3notes);

        String[] split1 = category.split("-");
        String[] split2 = split1[0].split(":");
        String top1categoryName = split2[0].toUpperCase();

        PerfumeCategory top1category = perfumeCategoryRepository.findByCategoryName(top1categoryName)
                .orElseThrow(PerfumeCategoryNotFoundException::new);

        ImageResult imageResult = ImageResult.builder()
                .user(user)
                .fileId(fileIdList.get(0))
                .perfumeCategory(top1category)
                .build();
        imageResultRepository.save(imageResult);

        List<Perfume> perfumesFromNotes = new ArrayList<>();

        List<Perfume> perfumesFromTop3Notes = perfumeRepository.findAllByTop3Notes(top3notes.get(0), top3notes.get(1), top3notes.get(2));

        perfumesFromNotes = perfumesFromTop3Notes;

        if(perfumesFromTop3Notes.isEmpty() || perfumesFromTop3Notes.size() < 5) {
            List<Perfume> perfumesFromTop2Notes = perfumeRepository.findAllByTop2Notes(top3notes.get(0), top3notes.get(1));
            perfumesFromNotes.addAll(perfumesFromTop2Notes);
            if(perfumesFromTop2Notes.isEmpty() || perfumesFromTop2Notes.size() < 5) {
                List<Perfume> perfumesFromTop1Notes = perfumeRepository.findAllByTop1Notes(top3notes.get(0));
                perfumesFromNotes.addAll(perfumesFromTop1Notes);
            }
        }

        HashMap<String, Double> indexSumMap = new HashMap<>();
        for(Perfume perfume : perfumesFromNotes) {
            double indexSum = 0;
            for(String note : top3notes) {
                String[] perfume_split = perfume.getNotes().split(",");
                for(int i=0; i<perfume_split.length; i++) {
                    if(perfume_split[i].contains(note)) {
                        indexSum += i;
                    }
                }
            }
            indexSumMap.put(perfume.getName(), indexSum);
            System.out.println(perfume.getName() + " : " + indexSum);
        }

        Map<String, Double> sortedIndexSumMap = sortedMapAsc(indexSumMap);

        System.out.println(sortedIndexSumMap);

        List<String> fivePerfumeName = sortedIndexSumMap.entrySet().stream()
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<Perfume> fivePerfume = new ArrayList<>();
        for(String name : fivePerfumeName) {
            Perfume perfume = perfumeRepository.findByPerfumeName(name).orElseThrow(UserNotFoundException::new);
            fivePerfume.add(perfume);
        }

        List<ResponsePerfumeAIListDto> collect = fivePerfume.stream().map(perfume -> new ResponsePerfumeAIListDto(perfume, messageSource,
                Arrays.asList(perfume.getNotes().split(",")))).collect(Collectors.toList());

        return new ResponsePerfumeLastAIListDto(collect, Arrays.asList(category.split("-")));
    }

    @Transactional
    public void deleteImage(Long userId, RequestDeleteImageDto dto) {
        ImageFile imageFile = imageFileRepository.findImageFileWithUserId(dto.getImageName(), userId).orElseThrow(ImageNotFoundException::new);
        amazonS3Service.deleteFile(imageFile.getFileId());
        imageFileRepository.delete(imageFile);
    }

    @Transactional
    public Page<ResponsePerfumeListDto> uploadImageTest(Long userId, RequestUploadFileDto dto, Pageable pageable) throws Exception{

        PerfumeCategory category = perfumeCategoryRepository.findByCategoryName("SPORTY")
                .orElseThrow(PerfumeCategoryNotFoundException::new);

        String[] split = category.getNotes().split(",");

        Page<Perfume> perfumeResult = perfumeRepository.findAllWithNotesOrderByLength(split[0], split[1],
                split[2], split[3], split[4], split[5], split[6], split[7], pageable);
        return perfumeResult.map(perfume -> new ResponsePerfumeListDto(perfume, messageSource));
    }

    private Map<String, Double> sortedMapDesc(Map<String, Double> map) {
        return map.entrySet().stream().sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    private Map<String, Double> sortedMapAsc(Map<String, Double> map) {
        return map.entrySet().stream().sorted(Map.Entry.<String, Double>comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

}
