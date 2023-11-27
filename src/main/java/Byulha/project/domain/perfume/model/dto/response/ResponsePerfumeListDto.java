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

    public ResponsePerfumeListDto(Perfume perfume, MessageSource messageSource) {
        this.id = perfume.getId();
        this.name = perfume.getName();
        this.company = perfume.getCompany();
        this.rating = perfume.getRating();
        this.forGender = messageSource.getMessage("perfume.for-gender." + perfume.getForGender().name().toLowerCase(), null, LocaleContextHolder.getLocale());
    }

}
