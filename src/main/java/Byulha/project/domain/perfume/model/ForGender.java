package Byulha.project.domain.perfume.model;

import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

@Getter
public enum ForGender {

    /**
     * 여성용
     */
    FOR_WOMEN,

    /**
     * 남성용
     */
    FOR_MEN,

    /**
     * 남녀공용
     */
    FOR_BOTH;

    public String getName(MessageSource messageSource) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage("perfume.for-gender." + this.name().toLowerCase(), null, locale);
    }
}
