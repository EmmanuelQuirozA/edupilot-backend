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
      pt.payment_through_id AS id,
      CASE WHEN :lang = 'en' THEN pt.name_en ELSE pt.name_es END AS name
    FROM payment_through pt
    JOIN users u_call
      ON u_call.user_id = :token_user_id
    WHERE
    (
      u_call.school_id IS NULL
      AND (
            :school_id IS NULL
            OR pt.school_id = :school_id
            OR pt.school_id IS NULL
          )
    )
    OR
    (
      u_call.school_id IS NOT NULL
      AND
      (
        pt.school_id IS NULL
        OR pt.school_id IN (
          SELECT school_id
          FROM (
            SELECT u_call.school_id AS school_id
            UNION ALL
            SELECT sc.school_id
            FROM schools sc
            WHERE sc.related_school_id = u_call.school_id
          ) x
          WHERE x.school_id IS NOT NULL
        )
      )
      AND
      (
        :school_id IS NULL
        OR pt.school_id = (
          CASE
            WHEN :school_id IN (
              SELECT school_id
              FROM (
                SELECT u_call.school_id AS school_id
                UNION ALL
                SELECT sc2.school_id
                FROM schools sc2
                WHERE sc2.related_school_id = u_call.school_id
              ) y
              WHERE y.school_id IS NOT NULL
            )
            THEN :school_id
            ELSE u_call.school_id
          END
        )
        OR pt.school_id IS NULL
      )
    )
    ORDER BY pt.payment_through_id DESC
    """,
    nativeQuery = true)
  List<PaymentThroughDto> findAllByLang(
      @Param("lang") String lang,
      @Param("token_user_id") Long tokenUserId,
      @Param("school_id") Long schoolId);
}
