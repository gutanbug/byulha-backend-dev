package Byulha.project.domain.excel.controller;

import Byulha.project.domain.excel.ExcelParser;
import Byulha.project.domain.excel.model.dto.request.RequestExcelDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/excel")
@Tag(name = "Excel", description = "Excel 관련 API")
public class ExcelController {

    private final ExcelParser excelParser;

    /**
     * Excel 데이터 파싱 후 DB에 저장
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void createExcelData(@Valid @ModelAttribute RequestExcelDto dto) throws Exception {
        excelParser.parseExcelData(dto);
    }
}
