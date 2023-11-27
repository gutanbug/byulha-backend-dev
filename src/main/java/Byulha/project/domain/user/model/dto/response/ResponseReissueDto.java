package Byulha.project.domain.user.model.dto.response;

import Byulha.project.global.auth.jwt.AuthenticationToken;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ResponseReissueDto {

    private final String accessToken;
    private final String refreshToken;

    public ResponseReissueDto(AuthenticationToken token) {
        this.accessToken = token.getAccessToken();
        this.refreshToken = token.getRefreshToken();
    }
}
