package Byulha.project.domain.excel.controller;

import Byulha.project.domain.excel.ExcelParser;
import Byulha.project.domain.excel.model.dto.request.RequestExcelDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelParser excelParser;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE,value = "/excel")
    public void createExcelData(@Valid @ModelAttribute RequestExcelDto dto) throws Exception {
        excelParser.parseExcelData(dto);
    }
}
