package Byulha.project.domain.django.model.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RequestSendToDjangoDto {
    private final String nickname;
    private final String fileId;

    @Builder
    public RequestSendToDjangoDto(String nickname, String fileId) {
        this.nickname = nickname;
        this.fileId = fileId;
    }
}
