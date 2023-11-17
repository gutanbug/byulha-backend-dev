package Byulha.project.infra.s3;

import Byulha.project.infra.s3.model.ImageFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {
}
