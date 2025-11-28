package com.monarchsolutions.sms.repository;

import com.monarchsolutions.sms.entity.Permission;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByRole_RoleIdAndModule_ModuleId(Long roleId, Long moduleId);

    @Query(value = """
            SELECT
              m.module_id AS moduleId,
              CASE WHEN :lang='en' THEN m.name_en ELSE m.name_es END AS moduleName,
              m.key AS moduleKey,
              mac.module_access_control_id AS moduleAccessControlId,
              mac.school_id AS schoolId,
              mac.enabled AS enabled,
              r.role_id AS roleId,
              r.role_name AS roleName,
              CASE WHEN :lang='en' THEN r.name_en ELSE r.name_es END AS roleNameDisplay,
              p.c AS createAllowed,
              p.r AS readAllowed,
              p.u AS updateAllowed,
              p.d AS deleteAllowed
            FROM permissions p
            JOIN modules m ON p.module_id = m.module_id
            JOIN roles r ON r.role_id = p.role_id
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
              AND m.key = :moduleKey
              AND r.role_id = :roleId
              AND (:onlyActive = FALSE OR mac.enabled = TRUE)
            ORDER BY mac.enabled DESC, m.module_id ASC
            """, nativeQuery = true)
    List<ModulePermissionProjection> findModulePermissionsForRole(
            @Param("tokenUserId") Long tokenUserId,
            @Param("roleId") Long roleId,
            @Param("moduleKey") String moduleKey,
            @Param("lang") String lang,
            @Param("onlyActive") boolean onlyActive
    );
}
