package Byulha.project.domain.perfume.model.dto.response;

import Byulha.project.domain.perfume.model.entity.Perfume;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.context.MessageSource;

import java.util.Arrays;
import java.util.List;

@Getter
public class ResponsePerfumeAIListDto extends ResponsePerfumeListDto {

    @Schema(description = "향수 노트", example = "Fruity,Woody")
    private final List<String> notes;

    public ResponsePerfumeAIListDto(Perfume perfume, MessageSource messageSource, List<String> notes) {
        super(perfume, messageSource);
        this.notes = notes;
    }
}
