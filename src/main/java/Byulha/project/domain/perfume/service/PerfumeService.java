package Byulha.project.domain.perfume.service;

import Byulha.project.domain.perfume.model.PerfumeSpec;
import Byulha.project.domain.perfume.model.dto.response.ResponsePerfumeDetailDto;
import Byulha.project.domain.perfume.model.dto.response.ResponsePerfumeListDto;
import Byulha.project.domain.perfume.model.entity.Perfume;
import Byulha.project.domain.perfume.model.entity.PerfumeCategory;
import Byulha.project.domain.perfume.repository.PerfumeCategoryRepository;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PerfumeService {

    private final PerfumeRepository perfumeRepository;
    private final PerfumeCategoryRepository perfumeCategoryRepository;
    private final MessageSource messageSource;

    public Page<ResponsePerfumeListDto> getPerfumeList(String name,
                                                       String company,
                                                       String forGender,
                                                       String sillage,
                                                       String longevity,
                                                       boolean isDesc,
                                                       Pageable pageable) {
        Specification<Perfume> spec = PerfumeSpec.withName(name);
        spec = spec.and(PerfumeSpec.withCompany(company));
        spec = spec.and(PerfumeSpec.withForGender(forGender));
        spec = spec.and(PerfumeSpec.withSilage(sillage));
        spec = spec.and(PerfumeSpec.withLongevity(longevity));
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
        List<String> notesList = Arrays.stream(perfume.getNotes().trim()
                .split(",")).collect(Collectors.toList());

        return new ResponsePerfumeDetailDto(perfume, notesList, messageSource);
    }

    public Set<String> getUniqueNotes() {
        Set<String> uniqueStrings = new HashSet<>();

        List<String> notesList = perfumeRepository.findAllWithNotes();

        for(String note : notesList) {
            String[] split = note.split(",");
            for(String s: split) {
                String[] notes = s.split(":");
                uniqueStrings.add(notes[0].trim());
            }
        }
        return uniqueStrings;
    }

    public void createPerfumeCategory() {
        //TODO : 카테고리 이름, 노트 종류대로 리스트화해서 한 번에 DB에 넣는 서비스 로직 추가
        List<String> categoryName = Arrays.asList("CUTE", "SENSUAL", "INNOCENT", "ELEGANT", "ANDROGYNOUS", "SEXY",
                                                "SPORTY", "PROFOUND", "MANLY", "SOPHISTICATED", "CASUAL");
        List<String> categoryNotes = Arrays.asList(
                "Citrus,Sour,Fruity,Cherry,Nutty,Yellow Floral,Lavender,Violet",
                "Tuberose,White Floral,Iris,Rose,Warm Spicy,Cacao,Vanilla,Caramel",
                "Lavender,Iris,Violet,Floral,Herbal,Green,Honey,Sweet",
                "Yellow Floral,Tuberose,White Floral,Rose,Cinnamon,Soft Spicy,Woody,Vanilla",
                "Aromatic,Mossy,Earthy,Green,Woody,Musky,Leather,Aquatic",
                "Warm Spicy,Soft Spicy,Vanilla,Caramel,Animalic,Amber,Musky,Woody",
                "Aromatic,Conifer,Marine,Mineral,Green,Aquatic,Citrus,Fruity",
                "Cinnamon,Oud,Woody,Amber,Musky,Leather,Vanilla,Tobacco",
                "Animalic,Leather,Rum,Whiskey,Woody,Iris,Cacao,Lavender",
                "Citrus,Green,Herbal,Mineral,Aldehydic,Woody,Musky,Vanilla",
                "Aromatic,Green,Herbal,Beverage,Nutty,Honey,Sweet,Vanilla"
        );
        for (int i = 0; i < categoryName.size(); i++) {
            PerfumeCategory perfumeCategory = PerfumeCategory.builder()
                    .categoryName(categoryName.get(i))
                    .notes(categoryNotes.get(i))
                    .build();
            perfumeCategoryRepository.save(perfumeCategory);
        }
    }
}
