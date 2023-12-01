package Byulha.project.domain.perfume.repository;

import Byulha.project.domain.perfume.model.entity.PerfumeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PerfumeCategoryRepository extends JpaRepository<PerfumeCategory, Long> {

    @Query("select p from PerfumeCategory p where p.categoryName = :categoryName")
    Optional<PerfumeCategory> findByCategoryName(String categoryName);
}
