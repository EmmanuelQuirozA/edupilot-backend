package com.monarchsolutions.sms.service;

import com.monarchsolutions.sms.dto.permission.ModulePermissionResponse;
import com.monarchsolutions.sms.repository.PermissionProcedureRepository;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.sql.SQLException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PermissionService {

    private final PermissionProcedureRepository permissionProcedureRepository;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PermissionService(
            PermissionProcedureRepository permissionProcedureRepository,
            NamedParameterJdbcTemplate jdbcTemplate
    ) {
        this.permissionProcedureRepository = permissionProcedureRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Check whether the role has permission to perform an action on a module.
     *
     * @param roleId    role identifier coming from JWT
     * @param moduleKey logical module key (e.g., students, payments)
     * @param action    CRUD action (c, r, u, d)
     * @return true if permission exists and is enabled
     */
    @Transactional(readOnly = true)
    public boolean hasPermission(Long roleId, String moduleKey, String action) {
        if (roleId == null || moduleKey == null || action == null) {
            return false;
        }

        String sql = """
                SELECT p.c AS createAllowed, p.r AS readAllowed, p.u AS updateAllowed, p.d AS deleteAllowed
                FROM permissions p
                JOIN modules m ON p.module_id = m.module_id
                WHERE p.role_id = :roleId
                  AND m.key = :moduleKey
                LIMIT 1
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("roleId", roleId)
                .addValue("moduleKey", moduleKey);

        Map<String, Object> permission;
        try {
            permission = jdbcTemplate.queryForMap(sql, params);
        } catch (EmptyResultDataAccessException ex) {
            return false;
        }

        String normalizedAction = action.toLowerCase(Locale.ROOT);
        return switch (normalizedAction) {
            case "c" -> byteToBoolean((Byte) permission.get("createAllowed"));
            case "r" -> byteToBoolean((Byte) permission.get("readAllowed"));
            case "u" -> byteToBoolean((Byte) permission.get("updateAllowed"));
            case "d" -> byteToBoolean((Byte) permission.get("deleteAllowed"));
            default -> false;
        };
    }

    @Transactional(readOnly = true)
    public List<ModulePermissionResponse> getModulePermissionsForUser(
            Long tokenUserId,
            String moduleKey,
            String lang,
            boolean onlyActive
    ) {
        if (tokenUserId == null || moduleKey == null || moduleKey.isBlank()) {
            return List.of();
        }

        String sql = """
          (
            SELECT
                m.module_id AS moduleId,
                CASE WHEN :lang = 'en' THEN m.name_en ELSE m.name_es END AS moduleName,
                m.`key` AS moduleKey,
                mac.module_access_control_id AS moduleAccessControlId,
                mac.school_id AS schoolId,
                mac.enabled AS enabled,
                u.role_id AS roleId,
                CASE WHEN :lang = 'en' THEN 'Administrator' ELSE 'Administrador' END AS roleNameDisplay,
                1 AS createAllowed,
                1 AS readAllowed,
                1 AS updateAllowed,
                1 AS deleteAllowed,
                NULL AS roleName
            FROM users u
            JOIN modules m ON m.`key` COLLATE utf8mb4_unicode_ci = :moduleKey COLLATE utf8mb4_unicode_ci
            LEFT JOIN module_access_control mac ON mac.module_id = m.module_id
            WHERE u.user_id = :tokenUserId
              AND u.role_id IN (0,1)
            LIMIT 1
        )
        UNION ALL
        (
            SELECT
                m.module_id AS moduleId,
                CASE WHEN :lang = 'en' THEN m.name_en ELSE m.name_es END AS moduleName,
                m.`key` AS moduleKey,
                mac.module_access_control_id AS moduleAccessControlId,
                mac.school_id AS schoolId,
                mac.enabled AS enabled,
                r.role_id AS roleId,
                CASE WHEN :lang = 'en' THEN r.name_en ELSE r.name_es END AS roleNameDisplay,
                p.c AS createAllowed,
                p.r AS readAllowed,
                p.u AS updateAllowed,
                p.d AS deleteAllowed,
                r.name_en AS roleName
            FROM permissions p
            JOIN modules m ON p.module_id = m.module_id
            JOIN roles r ON r.role_id = p.role_id
            JOIN module_access_control mac ON m.module_id = mac.module_id
            JOIN users u ON u.role_id = r.role_id
            WHERE u.user_id = :tokenUserId
              AND u.role_id NOT IN (0,1)
              AND mac.school_id IN (
                  SELECT school_id FROM (
                      SELECT u2.school_id FROM users u2 WHERE u2.user_id = :tokenUserId
                      UNION ALL
                      SELECT s.related_school_id FROM users u2
                      JOIN schools s ON u2.school_id = s.school_id
                      WHERE u2.user_id = :tokenUserId
                  ) x
                  WHERE school_id IS NOT NULL
              )
              AND m.`key` COLLATE utf8mb4_unicode_ci = :moduleKey COLLATE utf8mb4_unicode_ci
              AND (:onlyActive = FALSE OR mac.enabled = TRUE)
            ORDER BY mac.enabled DESC, m.module_id ASC
            LIMIT 1
        )
        LIMIT 1
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tokenUserId", tokenUserId)
                .addValue("moduleKey", moduleKey)
                .addValue("lang", lang)
                .addValue("onlyActive", onlyActive);

        return jdbcTemplate.query(
                sql,
                params,
                (rs, rowNum) -> {
                    ModulePermissionResponse response = new ModulePermissionResponse();
                    response.setModuleId(rs.getLong("moduleId"));
                    response.setModuleName(rs.getString("moduleName"));
                    response.setModuleKey(rs.getString("moduleKey"));
                    response.setModuleAccessControlId(rs.getLong("moduleAccessControlId"));
                    response.setSchoolId(rs.getLong("schoolId"));
                    response.setEnabled(byteToBoolean(rs.getByte("enabled")));
                    response.setRoleId(rs.getLong("roleId"));
                    response.setRoleName(rs.getString("roleName"));
                    response.setRoleNameDisplay(rs.getString("roleNameDisplay"));
                    response.setCreateAllowed(byteToBoolean(rs.getByte("createAllowed")));
                    response.setReadAllowed(byteToBoolean(rs.getByte("readAllowed")));
                    response.setUpdateAllowed(byteToBoolean(rs.getByte("updateAllowed")));
                    response.setDeleteAllowed(byteToBoolean(rs.getByte("deleteAllowed")));
                    return response;
                }
        );
    }

    private Boolean byteToBoolean(Byte value) {
        return value != null && value != 0;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getModuleAccessList(
            Long tokenUserId,
            Long roleId,
            Long schoolId,
            String moduleKey,
            String lang
    ) throws SQLException {
        return permissionProcedureRepository.getModuleAccessList(tokenUserId, roleId, schoolId, moduleKey, lang);
    }

    @Transactional
    public Map<String, Object> createPermission(Long tokenUserId, Object payload, String lang) throws Exception {
        return permissionProcedureRepository.createPermission(tokenUserId, payload, lang);
    }

    @Transactional
    public Map<String, Object> updatePermission(Long tokenUserId, Long permissionId, Object payload, String lang)
            throws Exception {
        return permissionProcedureRepository.updatePermission(tokenUserId, permissionId, payload, lang);
    }
}
