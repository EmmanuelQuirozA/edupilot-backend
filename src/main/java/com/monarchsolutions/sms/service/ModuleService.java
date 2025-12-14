package com.monarchsolutions.sms.service;

import com.monarchsolutions.sms.dto.catalogs.ModuleAccessResponse;
import java.util.List;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ModuleService {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ModuleService(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ModuleAccessResponse> getAccessibleModules(
            Long tokenUserId,
            String searchTerm,
            boolean onlyActive,
            String lang,
            String moduleKey
    ) {
        String sanitizedSearch = (searchTerm == null || searchTerm.isBlank()) ? null : searchTerm;
        String sanitizedModuleKey = (moduleKey == null || moduleKey.isBlank()) ? null : moduleKey;

        String sql = """
                SELECT
                  m.module_id AS moduleId,
                  CASE WHEN :lang='en' THEN m.name_en ELSE m.name_es END AS moduleName,
                  m.key AS moduleKey,
                  mac.module_access_control_id AS moduleAccessControlId,
                  mac.school_id AS schoolId,
                  mac.enabled AS enabled,
                  m.sort_order AS sortOrder
                FROM modules m
                JOIN module_access_control mac ON m.module_id = mac.module_id
                WHERE
                  (
                    (
                      (SELECT u.school_id FROM users u WHERE u.user_id = :tokenUserId) IS NULL
                      AND mac.school_id IS NULL
                    )
                    OR
                    (
                        (SELECT u.school_id FROM users u WHERE u.user_id = :tokenUserId) IS NOT NULL
                        AND mac.school_id IN (
                          SELECT school_id FROM (
                              SELECT u.school_id AS school_id
                              FROM users u
                              WHERE u.user_id = :tokenUserId

                              UNION ALL

                              SELECT s.related_school_id AS school_id
                              FROM users u
                              JOIN schools s ON u.school_id = s.school_id
                              WHERE u.user_id = :tokenUserId
                          ) AS user_schools
                          WHERE school_id IS NOT NULL
                      )
                  )
                )
                AND (:searchTerm IS NULL OR (CASE WHEN :lang='en' THEN m.name_en ELSE m.name_es END) LIKE CONCAT('%', :searchTerm, '%'))
                AND (:moduleKey IS NULL OR m.key = :moduleKey)
                AND (:onlyActive = FALSE OR mac.enabled = TRUE)
                ORDER BY mac.enabled DESC, m.sort_order ASC
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tokenUserId", tokenUserId)
                .addValue("searchTerm", sanitizedSearch)
                .addValue("onlyActive", onlyActive)
                .addValue("lang", lang)
                .addValue("moduleKey", sanitizedModuleKey);

        return jdbcTemplate.query(
                sql,
                params,
                (rs, rowNum) -> {
                    ModuleAccessResponse response = new ModuleAccessResponse();
                    response.setModuleId(rs.getLong("moduleId"));
                    response.setModuleName(rs.getString("moduleName"));
                    response.setModuleKey(rs.getString("moduleKey"));
                    response.setModuleAccessControlId(rs.getLong("moduleAccessControlId"));
                    response.setSchoolId(rs.getLong("schoolId"));
                    response.setEnabled(rs.getBoolean("enabled"));
                    response.setSortOrder(rs.getInt("sortOrder"));
                    return response;
                }
        );
    }
}
