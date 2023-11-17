package Byulha.project.infra.s3.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class RequestUploadFileDto {

    @Schema(description = "이미지 파일 목록")
    private final List<MultipartFile> files;

    public RequestUploadFileDto(List<MultipartFile> files) {
        this.files = Objects.requireNonNullElseGet(files, ArrayList::new);
    }
}
