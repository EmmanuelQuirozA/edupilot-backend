package com.monarchsolutions.sms.repository.catalogs;

import com.monarchsolutions.sms.dto.catalogs.PaymentThroughDto;
import com.monarchsolutions.sms.entity.PaymentThroughEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;


public interface PaymentThroughRepository extends JpaRepository<PaymentThroughEntity, Long> {
  
  @Query(value = """
    SELECT
      pt.payment_through_id               AS id,
      CASE WHEN :lang = 'en' THEN pt.name_en ELSE pt.name_es END           AS name
    FROM payment_through pt
    ORDER BY pt.payment_through_id
    """,
    nativeQuery = true)
  List<PaymentThroughDto> findAllByLang(@Param("lang") String lang);
}
