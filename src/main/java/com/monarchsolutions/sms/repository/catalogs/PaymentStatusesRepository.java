package com.monarchsolutions.sms.repository.catalogs;

import com.monarchsolutions.sms.dto.catalogs.PaymentStatusesDto;
import com.monarchsolutions.sms.entity.PaymentConceptsEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaymentStatusesRepository extends JpaRepository<PaymentConceptsEntity, Long> {

  @Query(value = """
    SELECT
      pt.payment_status_id               AS id,
      CASE WHEN :lang = 'en' THEN pt.name_en ELSE pt.name_es END           AS name
    FROM payment_status pt
    ORDER BY pt.payment_status_id
    """,
    nativeQuery = true)
  List<PaymentStatusesDto> findAllByLang(@Param("lang") String lang);
}
