package Byulha.project.domain.user.model.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RequestDeleteImageDto {
    private final String imageName;
}
