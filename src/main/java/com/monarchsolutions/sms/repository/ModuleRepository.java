package com.monarchsolutions.sms.repository;

import com.monarchsolutions.sms.entity.Module;
import com.monarchsolutions.sms.repository.ModuleAccessProjection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    Optional<Module> findByKey(String key);

    @Query(value = """
            SELECT
              m.module_id AS moduleId,
              CASE WHEN :lang='en' THEN m.name_en ELSE m.name_es END AS moduleName,
              m.key AS moduleKey,
              mac.module_access_control_id AS moduleAccessControlId,
              mac.school_id AS schoolId,
              mac.enabled AS enabled
            FROM modules m
            JOIN module_access_control mac ON m.module_id = mac.module_id
            WHERE mac.school_id IN (
                SELECT school_id
                FROM (
                    (SELECT u.school_id AS school_id
                    FROM users u
                    WHERE u.user_id = :tokenUserId
                    LIMIT 1)

                    UNION ALL

                    (SELECT s.related_school_id AS school_id
                    FROM users u
                    JOIN schools s ON u.school_id = s.school_id
                    WHERE u.user_id = :tokenUserId
                    LIMIT 1)
                ) AS user_schools
                WHERE school_id IS NOT NULL
            )
              AND (:searchTerm IS NULL OR (CASE WHEN :lang='en' THEN m.name_en ELSE m.name_es END) LIKE CONCAT('%', :searchTerm, '%'))
              AND (:moduleKey IS NULL OR m.key = :moduleKey)
              AND (:onlyActive = FALSE OR mac.enabled = TRUE)
            ORDER BY mac.enabled DESC, m.module_id ASC
            """, nativeQuery = true)
    List<ModuleAccessProjection> findModulesForUserSchools(
            @Param("tokenUserId") Long tokenUserId,
            @Param("searchTerm") String searchTerm,
            @Param("onlyActive") boolean onlyActive,
            @Param("lang") String lang,
            @Param("moduleKey") String moduleKey
    );
}
