package Byulha.project.domain.flask.model.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RequestSendToFlaskDto {
    private final String nickname;
    private final String fileId;
}
