package Byulha.project.domain.perfume.model.dto.response;

import Byulha.project.domain.perfume.model.entity.Perfume;
import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.List;

@Getter
public class ResponsePerfumeDetailDto {

    private final String perfumeUrl;

    private final String name;

    private final String company;

    private final List<String> notes;

    private final String rating;

    private final String forGender;

    private final String sillage;

    private final String longevity;

    private final String priceValue;

    private final String perfumeImage;

    private final String perfumeDetail;


    public ResponsePerfumeDetailDto(Perfume perfume, List<String> notesList, MessageSource messageSource) {
        this.perfumeUrl = perfume.getPerfumeUrl();
        this.name = perfume.getName();
        this.company = perfume.getCompany();
        this.notes = notesList;
        this.rating = String.valueOf(perfume.getRating());
        this.forGender = messageSource.getMessage("perfume.for-gender." + perfume.getForGender().name().toLowerCase(), null, LocaleContextHolder.getLocale());
        this.sillage = messageSource.getMessage("perfume.sillage." + perfume.getSillage().name().toLowerCase(), null, LocaleContextHolder.getLocale());
        this.longevity = messageSource.getMessage("perfume.longevity." + perfume.getLongevity().name().toLowerCase(), null, LocaleContextHolder.getLocale());
        this.priceValue = messageSource.getMessage("perfume.price-value." + perfume.getPriceValue().name().toLowerCase(), null, LocaleContextHolder.getLocale());
        this.perfumeImage = perfume.getPerfumeImage();
        this.perfumeDetail = perfume.getPerfumeDetail();
    }
}
