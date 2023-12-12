package Byulha.project.domain.user.model.dto.response;

import Byulha.project.domain.perfume.model.dto.response.ResponsePerfumeAIListDto;
import lombok.Getter;

import java.util.List;

@Getter
public class ResponsePerfumeLastAIListDto {

    private final List<ResponsePerfumeAIListDto> perfumes;

    private final List<String> moods;

    public ResponsePerfumeLastAIListDto(List<ResponsePerfumeAIListDto> dto, List<String> moods) {
        this.perfumes = dto;
        this.moods = moods;
    }
}
