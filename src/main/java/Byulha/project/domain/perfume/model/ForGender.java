package Byulha.project.domain.perfume.model;

import lombok.Getter;

@Getter
public enum ForGender {

    /**
     * 여성용
     */
    FOR_WOMEN("for women"),

    /**
     * 남성용
     */
    FOR_MEN("for men"),

    /**
     * 남녀공용
     */
    FOR_BOTH("for women and men");

    private final String for_gender;

    ForGender(String for_gender) {
        this.for_gender = for_gender;
    }


}
