package Byulha.project.domain.perfume.service;

import Byulha.project.domain.perfume.model.PerfumeSpec;
import Byulha.project.domain.perfume.model.dto.response.ResponsePerfumeDetailDto;
import Byulha.project.domain.perfume.model.dto.response.ResponsePerfumeListDto;
import Byulha.project.domain.perfume.model.entity.Perfume;
import Byulha.project.domain.perfume.repository.PerfumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class PerfumeService {

    private final PerfumeRepository perfumeRepository;
    private final MessageSource messageSource;

    public Page<ResponsePerfumeListDto> getPerfumeList(String forGender,
                                                       String sillage,
                                                       String priceValue,
                                                       boolean isDesc,
                                                       Pageable pageable) {
        Specification<Perfume> spec = PerfumeSpec.withForGender(forGender);
        spec = spec.and(PerfumeSpec.withSillage(sillage));
        spec = spec.and(PerfumeSpec.withPriceValue(priceValue));
        spec = spec.and(PerfumeSpec.withIsDesc(isDesc));

        return getResponsePerfumeListDto(pageable, spec);
    }

    private Page<ResponsePerfumeListDto> getResponsePerfumeListDto(Pageable pageable, Specification<Perfume> spec) {
        if (spec == null) {
            spec = Specification.where(null);
        }

        Page<Perfume> list = perfumeRepository.findAll(spec, pageable);
        return list.map(perfume -> new ResponsePerfumeListDto(perfume, messageSource));
    }

    public ResponsePerfumeDetailDto getPerfumeDetail(Long perfumeId) {
        Perfume perfume = perfumeRepository.findOneById(perfumeId);
        return new ResponsePerfumeDetailDto(perfume, messageSource);
    }

    public Set<String> getNotes() {
        Set<String> uniqueStrings = new HashSet<>();

        List<String> notesList = perfumeRepository.findAllWithNotes();
        for (String notes : notesList) {
            String[] split = notes.split(",");
            for(String note : split) {
                uniqueStrings.add(note.trim());
            }
        }
        return uniqueStrings;
    }
}
