package com.selection_point.repository;

import com.selection_point.dto.SchoolScatterDTO;
import com.selection_point.entity.School;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;


public interface SchoolRepository extends JpaRepository<School, Long> {


    @Query("SELECT s FROM School s WHERE UPPER(TRIM(s.offerCode)) = UPPER(TRIM(:code))")
    Optional<School> findByOfferCode(@Param("code") String code);

    // TOTAL SCHOOLS
    long count();

    @Query("select count(s) from School s")
    long totalSchools();

}
