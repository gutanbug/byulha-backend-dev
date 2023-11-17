package Byulha.project.domain.user.model.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

import static lombok.AccessLevel.*;


@Getter
@RequiredArgsConstructor(access = PROTECTED)
public class RequestReissueDto {

    @NotBlank
    private final String refreshToken;

}
