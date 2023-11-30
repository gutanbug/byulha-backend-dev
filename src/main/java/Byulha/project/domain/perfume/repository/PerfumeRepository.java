package Byulha.project.domain.perfume.repository;

import Byulha.project.domain.perfume.model.entity.Perfume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PerfumeRepository extends JpaRepository<Perfume, Long>, JpaSpecificationExecutor<Perfume> {

//    @EntityGraph(attributePaths = {"perfume", "perfume.forGender", "perfume.sillage", "perfume.priceValue"})
    Page<Perfume> findAll(Specification<Perfume> spec, Pageable pageable);

    @Query("select p from Perfume p where p.id = :perfumeId")
    Perfume findOneById(Long perfumeId);

    @Query("select p.notes from Perfume p")
    List<String> findAllWithNotes();
}
