package com.monarchsolutions.sms.repository.catalogs;

import com.monarchsolutions.sms.dto.catalogs.ScholarLevelsDto;
import com.monarchsolutions.sms.entity.ScholarLevelsEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;


public interface ScholarLevelsRepository extends JpaRepository<ScholarLevelsEntity, Long> {
  
  @Query(value = """
    SELECT
      sl.scholar_level_id               AS id,
      CASE WHEN :lang = 'en' THEN sl.name_en ELSE sl.name_es END           AS name
    FROM scholar_levels sl
    ORDER BY sl.scholar_level_id
    """,
    nativeQuery = true)
  List<ScholarLevelsDto> findAllByLang(@Param("lang") String lang);
}
