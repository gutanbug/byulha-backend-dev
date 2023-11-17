package Byulha.project.infra.s3.model;

import Byulha.project.global.base.BaseEntity;
import Byulha.project.user.model.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static lombok.AccessLevel.*;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class ImageFile extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "image_file_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String contentType;

    private String fileName;

    @Builder
    private ImageFile(User user, String contentType, String fileName) {
        this.user = user;
        this.contentType = contentType;
        this.fileName = fileName;
    }
}
