package Byulha.project.domain.perfume.model.entity;

import Byulha.project.domain.perfume.model.ForGender;
import Byulha.project.domain.perfume.model.PriceValue;
import Byulha.project.domain.perfume.model.Sillage;
import Byulha.project.global.base.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.*;

@Entity
@Table(name = "perfume")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Perfume extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "perfume_id")
    private Long id;

    @NotNull
    private String perfumeUrl;

    @NotNull
    private String name;

    @NotNull
    private String company;

    @NotNull
    @Lob
    private String notes;

    @NotNull
    private double rating;

    @Enumerated(EnumType.STRING)
    private ForGender forGender;

    @Enumerated(EnumType.STRING)
    private Sillage sillage;

    @Enumerated(EnumType.STRING)
    private PriceValue priceValue;

    @NotNull
    private String perfumeImage;

    @NotNull
    private String thumbnailImage;

    @Builder
    private Perfume(@NotNull String perfumeUrl,
                    @NotNull String name,
                    @NotNull String company,
                    @NotNull String notes,
                    @NotNull double rating,
                    @NotNull ForGender forGender,
                    @NotNull Sillage sillage,
                    @NotNull PriceValue priceValue,
                    @NotNull String perfumeImage,
                    @NotNull String thumbnailImage) {
        this.perfumeUrl = perfumeUrl;
        this.name = name;
        this.company = company;
        this.notes = notes;
        this.rating = rating;
        this.forGender = forGender;
        this.sillage = sillage;
        this.priceValue = priceValue;
        this.perfumeImage = perfumeImage;
        this.thumbnailImage = thumbnailImage;
    }
}
