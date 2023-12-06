package Byulha.project.domain.perfume.model.dto.response;

import Byulha.project.domain.perfume.model.entity.Perfume;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@Getter
public class ResponsePerfumeListDto {

    @Schema(description = "향수 아이디", example = "1")
    private final Long id;

    @Schema(description = "향수 이름", example = "향수 이름")
    private final String name;

    @Schema(description = "향수 회사", example = "향수 회사")
    private final String company;

    @Schema(description = "향수 평점", example = "4.5")
    private final double rating;

    @Schema(description = "향수 사용 성별", example = "남성")
    private final String forGender;

    @Schema(description = "향수 지속성", example = "LONG_LASTING")
    private final String longevity;

    @Schema(description = "향수 잔향", example = "남성")
    private final String sillage;

    @Schema(description = "향수 썸네일 이미지 경로", example = "https://www.naver.com")
    private final String thumbnailUrl;

    public ResponsePerfumeListDto(Perfume perfume, MessageSource messageSource) {
        this.id = perfume.getId();
        this.name = perfume.getName();
        this.company = perfume.getCompany();
        this.rating = perfume.getRating();
        this.forGender = messageSource.getMessage("perfume.for-gender." + perfume.getForGender().name().toLowerCase(), null, LocaleContextHolder.getLocale());
        this.sillage = messageSource.getMessage("perfume.sillage." + perfume.getSillage().name().toLowerCase(), null, LocaleContextHolder.getLocale());
        this.longevity = messageSource.getMessage("perfume.longevity." + perfume.getLongevity().name().toLowerCase(), null, LocaleContextHolder.getLocale());
        this.thumbnailUrl = perfume.getThumbnailImage();
    }

}
