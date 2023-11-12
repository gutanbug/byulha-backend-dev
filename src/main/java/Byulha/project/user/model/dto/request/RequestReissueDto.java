package Byulha.project.user.model.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;


@Getter
@RequiredArgsConstructor
public class RequestReissueDto {

    @NotBlank
    private final String refreshToken;

}
