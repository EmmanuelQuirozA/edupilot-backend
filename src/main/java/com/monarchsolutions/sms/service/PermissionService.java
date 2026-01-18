package com.monarchsolutions.sms.service;

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
    public boolean hasPermission(Long schoolId, Long roleId, String moduleKey, String action) {
        if (roleId == null || moduleKey == null || action == null) {
            return false;
        }

        String sql = """
                SELECT
                COALESCE(o.c, rp.c, 0) AS createAllowed,
                COALESCE(o.r, rp.r, 0) AS readAllowed,
                COALESCE(o.u, rp.u, 0) AS updateAllowed,
                COALESCE(o.d, rp.d, 0) AS deleteAllowed
                FROM modules m
                LEFT JOIN role_permissions rp
                ON rp.module_id = m.module_id
                AND rp.role_id   = :roleId
                AND rp.enabled   = 1
                LEFT JOIN school_role_permission_overrides o
                ON :schoolId IS NOT NULL
                AND o.school_id = :schoolId
                AND o.role_id   = :roleId
                AND o.module_id = m.module_id
                AND o.enabled   = 1
                WHERE m.`key` = :moduleKey
                LIMIT 1;
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("schoolId", schoolId)
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
            case "c" -> mapToBoolean(permission.get("createAllowed"));
            case "r" -> mapToBoolean(permission.get("readAllowed"));
            case "u" -> mapToBoolean(permission.get("updateAllowed"));
            case "d" -> mapToBoolean(permission.get("deleteAllowed"));
            default -> false;
        };
    }

    private boolean mapToBoolean(Object value) {
        if (value == null) {
            return false;
        }

        if (value instanceof Boolean boolValue) {
            return boolValue;
        }

        if (value instanceof Number numberValue) {
            return numberValue.intValue() != 0;
        }

        return false;
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

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMenuAccessList(
            Long tokenUserId,
            String lang
    ) throws SQLException {
        return permissionProcedureRepository.getMenuAccessList(tokenUserId, lang);
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
