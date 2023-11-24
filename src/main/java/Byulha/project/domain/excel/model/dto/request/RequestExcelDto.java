package Byulha.project.domain.excel.model.dto.request;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.function.Supplier;

@Getter
public class RequestExcelDto {
    MultipartFile excelFile;

    public RequestExcelDto(MultipartFile excelFile) {
        this.excelFile = Objects.requireNonNull(excelFile);
    }
}
