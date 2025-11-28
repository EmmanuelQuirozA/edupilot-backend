package com.monarchsolutions.sms.repository;

import com.monarchsolutions.sms.entity.Role;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleEntityRepository extends JpaRepository<Role, Long> {
    @Query(value = """
            SELECT 
              r.role_id,
              r.role_name,
              CASE WHEN :lang='en' THEN r.name_en ELSE r.name_es END AS role_name_display,
              CASE WHEN :lang='en' THEN r.description_en ELSE r.description_es END AS role_description_display,
              r.enabled
            FROM roles r
            WHERE r.school_id IN (
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
              AND (:searchTerm IS NULL OR r.role_name LIKE CONCAT('%', :searchTerm, '%'))
              AND (:onlyActive = FALSE OR r.enabled = TRUE)
            ORDER BY r.enabled DESC, r.role_id ASC
            """, nativeQuery = true)
    List<Role> findRolesForUserSchools(
            @Param("tokenUserId") Long tokenUserId,
            @Param("searchTerm") String searchTerm,
            @Param("onlyActive") boolean onlyActive,
            @Param("lang") String lang
    );
}
