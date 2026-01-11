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
      pt.payment_concept_id AS id,
      CASE WHEN :lang = 'en' THEN pt.name_en ELSE pt.name_es END AS name,
      CASE WHEN :lang = 'en' THEN pt.description_en ELSE pt.description_es END AS description
    FROM payment_concepts pt
    JOIN users u_call
      ON u_call.user_id = :token_user_id
    WHERE
    (
      /* =======================
         CASO A: usuario SIN school_id (global/admin)
         ======================= */
      u_call.school_id IS NULL
      AND (
            :school_id IS NULL
            OR pt.school_id = :school_id
            OR pt.school_id IS NULL
          )
    )
    OR
    (
      /* =======================
         CASO B: usuario CON school_id
         ======================= */

      u_call.school_id IS NOT NULL
      AND
      (
        /* Globales siempre */
        pt.school_id IS NULL

        /* Conceptos de escuelas accesibles */
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
        /* Filtro opcional por escuela:
           - si viene :school_id, solo aplica si está dentro del acceso del usuario
           - si NO está dentro, cae a la escuela del usuario (safe default)
        */
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
    ORDER BY pt.payment_concept_id DESC
    """,
    nativeQuery = true)
  List<PaymentConceptsDto> findAllByLang(@Param("lang") String lang,
      @Param("token_user_id") Long tokenUserId,
      @Param("school_id") Long schoolId);
}
