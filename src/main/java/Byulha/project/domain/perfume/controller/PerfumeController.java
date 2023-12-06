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

import java.util.Set;


@RestController
@RequiredArgsConstructor
@RequestMapping("/perfume")
@Tag(name = "향수", description = "향수 관련 api")
public class PerfumeController {

    private final PerfumeService perfumeService;

    /**
     * 향수 목록 전체 조회
     *
     * @param pageable 페이징 size, sort, page
     * @return 페이징된 향수 목록
     */
    @GetMapping
    public ResponsePage<ResponsePerfumeListDto> getPerfumeList(@RequestParam(name = "FOR_MEN",required = false) String forGender,
                                                               @RequestParam(name = "Sillage",required = false) String sillage,
                                                               @RequestParam(name = "Longevity",required = false) String longevity,
                                                               @RequestParam(name = "isDesc", required = false) boolean isDesc,
                                                               @ParameterObject Pageable pageable) {
        Page<ResponsePerfumeListDto> list = perfumeService.getPerfumeList(forGender, sillage, longevity, isDesc,pageable);
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

    /**
     * 겹치지 않는 향수 노트 조회
     */
    @GetMapping("/notes")
    public Set<String> getNotes() {
        return perfumeService.getNotes();
    }

    /**
     * 향수 카테고리 생성(프론트X)
     *
     * 이 메서드는 한 번만 실행하면 되므로 실행 이후에는 사용하지 않는다.
     */
    @GetMapping("/create/category")
    public void createPerfumeCategory() {
        perfumeService.createPerfumeCategory();
    }


}
