package Byulha.project.domain.user.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestVerifyTokenCodeDto {

    @NotBlank
    @Schema(description = "재설정 토큰", example = "token")
    private final String token;

    @NotBlank
    @Schema(description = "인증 코드", example = "123456")
    private final String code;
}
