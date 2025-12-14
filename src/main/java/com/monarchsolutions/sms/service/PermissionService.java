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
