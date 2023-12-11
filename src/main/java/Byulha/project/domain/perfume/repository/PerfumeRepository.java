package Byulha.project.domain.perfume.repository;

import Byulha.project.domain.perfume.model.entity.Perfume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PerfumeRepository extends JpaRepository<Perfume, Long>, JpaSpecificationExecutor<Perfume> {

    Page<Perfume> findAll(Specification<Perfume> spec, Pageable pageable);

    @Query("select p from Perfume p where p.id = :perfumeId")
    Perfume findOneById(Long perfumeId);

    @Query("select p from Perfume p where p.name = :perfumeName")
    Optional<Perfume> findByPerfumeName(String perfumeName);

    @Query("select p.notes from Perfume p")
    List<String> findAllWithNotes();

    @Query("select p from Perfume p where p.notes like :note1% or p.notes like :note2%" +
            " or p.notes like :note3% or p.notes like :note4% or p.notes like :note5%" +
            " or p.notes like :note6% or p.notes like :note7% or p.notes like :note8%" +
            " order by length(p.notes) ")
    Page<Perfume> findAllWithNotesOrderByLength(String note1, String note2, String note3,
                                                String note4, String note5, String note6,
                                                String note7, String note8, Pageable pageable);

    @Query("select p from Perfume p where p.notes like %:s% and p.notes like %:s1% and p.notes like %:s2%")
    Page<Perfume> findAllByTop3Notes(String s, String s1, String s2, Pageable pageable);

    @Query("select p from Perfume p where p.notes like %:s% and p.notes like %:s1%")
    Page<Perfume> findAllByTop2Notes(String s, String s1, Pageable pageable);
}
