package Byulha.project.domain.flask.controller;

import Byulha.project.domain.flask.model.dto.request.RequestSendToFlaskDto;
import Byulha.project.domain.flask.service.FlaskService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Flask 관련 컨트롤러", description = "Flask 관련 기능 컨트롤러")
public class FlaskController {

    private final FlaskService flaskService;

    /**
     * Flask로 데이터 전송
     *
     * @param dto             요청 body
     */
    @PostMapping("/flask")
    public String sendToFlask(@RequestBody RequestSendToFlaskDto dto) throws JsonProcessingException {
        return flaskService.sendToFlask(dto);
    }
}
