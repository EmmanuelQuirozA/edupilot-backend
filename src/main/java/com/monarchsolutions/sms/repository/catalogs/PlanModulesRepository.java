package com.monarchsolutions.sms.repository.catalogs;

import com.monarchsolutions.sms.dto.catalogs.PlanModuleDto;
import com.monarchsolutions.sms.entity.Module;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlanModulesRepository extends JpaRepository<Module, Long> {

    @Query(value = """
        SELECT DISTINCT
            m.module_id AS moduleId,
            CASE 
                WHEN :lang = 'en' THEN m.name_en 
                ELSE m.name_es 
            END AS moduleName,
            m.`key` AS moduleKey,
            CASE 
                WHEN :lang = 'en' THEN m.description_en 
                ELSE m.description_es 
            END AS moduleDescription,
            m.icon
        FROM school_plans sp
        JOIN plan_modules pm ON sp.plan_id = pm.plan_id
        JOIN modules m ON pm.module_id = m.module_id

        /* ===========================
           CONTEXTO DE ESCUELA
           =========================== */
        LEFT JOIN schools s_sp ON sp.school_id = s_sp.school_id
        LEFT JOIN users u     ON u.user_id = :token_user_id
        LEFT JOIN schools s_u ON u.school_id = s_u.school_id

        WHERE
        (
            /* ======================================
               CASO 1: USUARIO TIENE ESCUELA
               ====================================== */
            u.school_id IS NOT NULL
            AND (
                sp.school_id = u.school_id
                OR sp.school_id = s_u.related_school_id
            )
        )
        OR
        (
            /* ======================================
               CASO 2: USUARIO SIN ESCUELA
               ====================================== */
            u.school_id IS NULL
            AND (
                sp.school_id = :p_school_id
                OR sp.school_id = (
                    SELECT related_school_id
                    FROM schools
                    WHERE school_id = :p_school_id
                )
            )
        )

        ORDER BY
            CASE 
                WHEN :lang = 'en' THEN m.name_en 
                ELSE m.name_es 
            END ASC
        """, nativeQuery = true)
    List<PlanModuleDto> findPlanModules(
            @Param("lang") String lang,
            @Param("token_user_id") Long tokenUserId,
            @Param("p_school_id") Long schoolId
    );
}
