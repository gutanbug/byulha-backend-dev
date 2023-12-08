package Byulha.project.domain.perfume.model.dto.response;

import Byulha.project.domain.perfume.model.entity.Perfume;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.context.MessageSource;

import java.util.Arrays;
import java.util.List;

@Getter
public class ResponsePerfumeAIListDto extends ResponsePerfumeListDto {

    @Schema(description = "향수 분위기", example = "향수 분위기")
    private final List<String> moods;

    public ResponsePerfumeAIListDto(Perfume perfume, MessageSource messageSource, String[] moods) {
        super(perfume, messageSource);
        this.moods = Arrays.asList(moods);
    }
}
