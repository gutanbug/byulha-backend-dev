package Byulha.project.domain.perfume.model.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "perfume_category")
public class PerfumeCategory {

    @Id
    @GeneratedValue
    private Long id;

    private String categoryName;

    private String notes;

    @Builder
    private PerfumeCategory(String categoryName, String notes) {
        this.categoryName = categoryName;
        this.notes = notes;
    }
}
