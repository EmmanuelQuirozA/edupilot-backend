package com.monarchsolutions.sms.repository.catalogs;

import com.monarchsolutions.sms.entity.PaymentConceptsEntity;

import com.monarchsolutions.sms.dto.catalogs.PaymentConceptsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaymentConceptsRepository extends JpaRepository<PaymentConceptsEntity, Long> {
  
  @Query(value = """
    SELECT
      pt.payment_concept_id               AS id,
      CASE WHEN :lang = 'en' THEN pt.name_en ELSE pt.name_es END           AS name,
      CASE WHEN :lang = 'en' THEN pt.description_en ELSE pt.description_es END AS description
    FROM payment_concepts pt
    ORDER BY pt.payment_concept_id
    """,
    nativeQuery = true)
  List<PaymentConceptsDto> findAllByLang(@Param("lang") String lang);
}