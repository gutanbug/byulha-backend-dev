package Byulha.project.domain.user.model.entity;


import Byulha.project.domain.perfume.model.entity.PerfumeCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "image_result")
public class ImageResult {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String fileId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfume_category_id")
    private PerfumeCategory perfumeCategory;

    @Builder
    private ImageResult(String fileId, User user, PerfumeCategory perfumeCategory) {
        this.user = user;
        this.fileId = fileId;
        this.perfumeCategory = perfumeCategory;
    }
}
