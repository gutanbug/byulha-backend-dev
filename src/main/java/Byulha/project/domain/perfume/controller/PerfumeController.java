package Byulha.project.domain.perfume.controller;

import Byulha.project.domain.perfume.model.dto.response.ResponsePerfumeDetailDto;
import Byulha.project.domain.perfume.model.dto.response.ResponsePerfumeListDto;
import Byulha.project.domain.perfume.service.PerfumeService;
import Byulha.project.global.model.dto.ResponsePage;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/perfume")
@Tag(name = "향수", description = "향수 관련 api")
public class PerfumeController {

    private final PerfumeService perfumeService;

    //TODO 향수 필터링도 포함하여 같이 구현
    /**
     * 향수 목록 전체 조회
     *
     * @param pageable 페이징 size, sort, page
     * @return 페이징된 향수 목록
     */
    @GetMapping
    public ResponsePage<ResponsePerfumeListDto> getPerfumeList(@RequestParam(name = "FOR_MEN",required = false) String forGender,
                                                               @RequestParam(name = "MODERATE",required = false) String sillage,
                                                               @RequestParam(name = "OVERPRICED",required = false) String priceValue,
                                                               @RequestParam(name = "isDesc", required = false) boolean isDesc,
                                                               @ParameterObject Pageable pageable) {
        Page<ResponsePerfumeListDto> list = perfumeService.getPerfumeList(forGender, sillage, priceValue, isDesc,pageable);
        return new ResponsePage<>(list);
    }

    /**
     * 향수 상세 조회
     *
     * @param perfumeId 향수 아이디
     * @return 향수 상세 정보
     */
    @PostMapping("{perfumeId}")
    public ResponsePerfumeDetailDto getPerfumeDetail(@PathVariable Long perfumeId) {
        return perfumeService.getPerfumeDetail(perfumeId);
    }
}
