package com.selection_point.repository;

import com.selection_point.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface BatchRepository extends JpaRepository<Batch, Long> {

    /* =========================================================
       🔥 FIND AVAILABLE BATCH (CORE LOGIC)
       - Same package
       - Strength < capacity
       - Oldest batch first
       - LOCKED to avoid race condition
       ========================================================= */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select b from Batch b
        where b.coursePackage = :pkg
        and b.currentStrength < b.capacity
        order by b.id asc
    """)
    Optional<Batch> findAvailableBatch(@Param("pkg") String pkg);


    /* =========================================================
       🔥 TOTAL BATCH COUNT (DASHBOARD)
       ========================================================= */
    @Query("select count(b) from Batch b")
    long totalBatches();


    /* =========================================================
       🔥 BATCH COUNT GROUPED BY SCHOOL
       ========================================================= */
    @Query("""
        select b.school.schoolName, count(b)
        from Batch b
        group by b.school.schoolName
    """)
    List<Object[]> batchesBySchool();


    /* =========================================================
       🔥 GET ALL BATCHES ORDERED (UI LIST)
       ========================================================= */
    @Query("""
        select b from Batch b
        order by b.coursePackage asc, b.batchName asc
    """)
    List<Batch> findAllOrdered();


    /* =========================================================
       🔥 FIND BATCHES BY PACKAGE (OPTIONAL / FUTURE)
       ========================================================= */
    List<Batch> findByCoursePackageOrderByIdAsc(String coursePackage);
}
