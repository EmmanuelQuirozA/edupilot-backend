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
      (
        /* ===============================
          ADMIN: role_id = 0 o 1
          Acceso total
        =============================== */
        SELECT
            m.module_id AS moduleId,
            CASE WHEN :lang = 'en'
                THEN m.name_en
                ELSE m.name_es
            END AS moduleName,
            m.`key` AS moduleKey,
            mac.module_access_control_id AS moduleAccessControlId,
            mac.school_id AS schoolId,
            mac.enabled AS enabled,
            u.role_id AS roleId,
            CASE WHEN :lang = 'en'
                THEN 'Administrator'
                ELSE 'Administrador'
            END AS roleNameDisplay,
            1 AS createAllowed,
            1 AS readAllowed,
            1 AS updateAllowed,
            1 AS deleteAllowed,
            m.icon
        FROM users u
        JOIN modules m
            ON m.`key` COLLATE utf8mb4_unicode_ci
            = :moduleKey COLLATE utf8mb4_unicode_ci
        LEFT JOIN module_access_control mac
            ON mac.module_id = m.module_id
        WHERE u.user_id = :tokenUserId
          AND u.role_id IN (0,1)
        LIMIT 1
    )

    UNION ALL

    (
        /* ===============================
          USUARIOS NORMALES
        =============================== */
        SELECT
            m.module_id AS moduleId,
            CASE WHEN :lang = 'en'
                THEN m.name_en
                ELSE m.name_es
            END AS moduleName,
            m.`key` AS moduleKey,
            mac.module_access_control_id AS moduleAccessControlId,
            mac.school_id AS schoolId,
            mac.enabled AS enabled,
            r.role_id AS roleId,
            CASE WHEN :lang = 'en'
                THEN r.name_en
                ELSE r.name_es
            END AS roleNameDisplay,
            p.c AS createAllowed,
            p.r AS readAllowed,
            p.u AS updateAllowed,
            p.d AS deleteAllowed,
            m.icon
        FROM permissions p
        JOIN modules m ON p.module_id = m.module_id
        JOIN roles r ON r.role_id = p.role_id
        JOIN module_access_control mac ON m.module_id = mac.module_id
        JOIN users u ON u.role_id = r.role_id
        WHERE u.user_id = :tokenUserId
          AND u.role_id NOT IN (0,1)

          /* === scope por escuela === */
          AND mac.school_id IN (
              SELECT school_id
              FROM (
                  SELECT u2.school_id
                  FROM users u2
                  WHERE u2.user_id = :tokenUserId

                  UNION ALL

                  SELECT s.related_school_id
                  FROM users u2
                  JOIN schools s ON u2.school_id = s.school_id
                  WHERE u2.user_id = :tokenUserId
              ) x
              WHERE school_id IS NOT NULL
          )

          AND m.`key` COLLATE utf8mb4_unicode_ci
              = :moduleKey COLLATE utf8mb4_unicode_ci

          AND (:onlyActive = FALSE OR mac.enabled = TRUE)

        ORDER BY mac.enabled DESC, m.module_id ASC
        LIMIT 1
    )

    LIMIT 1;
    """, nativeQuery = true)
    List<ModulePermissionProjection> findModulePermissionsForRole(
            @Param("tokenUserId") Long tokenUserId,
            @Param("moduleKey") String moduleKey,
            @Param("lang") String lang,
            @Param("onlyActive") boolean onlyActive
    );
}
