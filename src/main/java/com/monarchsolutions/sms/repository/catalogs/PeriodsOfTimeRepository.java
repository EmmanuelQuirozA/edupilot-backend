package com.monarchsolutions.sms.repository.catalogs;

import com.monarchsolutions.sms.dto.catalogs.PeriodOfTimeDto;
import com.monarchsolutions.sms.entity.PeriodOfTimeEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;


public interface PeriodsOfTimeRepository extends JpaRepository<PeriodOfTimeEntity, Long> {

    @Query(value = """
        SELECT 
            pot.period_of_time_id AS id,
            CASE WHEN :lang = 'en' THEN pot.name_en ELSE pot.name_es END AS name
        FROM period_of_time pot
        WHERE 
            (
                /* 1. Mostrar siempre los globales */
                pot.school_id IS NULL
            OR
                /* Usuario sin school_id → ver todo */
                (SELECT u.school_id FROM users u WHERE u.user_id = :token_user_id) IS NULL
            OR
                /* Usuario con school → filtrar por school o sub-schools */
                pot.school_id = (SELECT u.school_id FROM users u WHERE u.user_id = :token_user_id)
                OR 
            pot.school_id IN (
                    SELECT s.school_id 
                    FROM schools s 
                    WHERE s.related_school_id = (SELECT u.school_id FROM users u WHERE u.user_id = :token_user_id)
                )
            )
        ORDER BY pot.period_of_time_id DESC
        """,
        nativeQuery = true
    )
    List<PeriodOfTimeDto> findAllByLang(
        @Param("lang") String lang,
        @Param("token_user_id") Long tokenUserId
    );
}
