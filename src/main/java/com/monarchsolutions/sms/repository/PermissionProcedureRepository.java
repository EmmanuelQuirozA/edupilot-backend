package com.monarchsolutions.sms.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PermissionProcedureRepository {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ObjectMapper objectMapper;

    public List<Map<String, Object>> getModuleAccessList(Long tokenUserId, Long roleId, Long schoolId, String moduleKey, String lang)
            throws SQLException {
        String call = "{CALL getModuleAccessList(?,?,?,?,?)}";
        List<Map<String, Object>> permissions = new ArrayList<>();

        try (Connection conn = dataSource.getConnection(); CallableStatement stmt = conn.prepareCall(call)) {
            int idx = 1;
            if (tokenUserId != null) {
                stmt.setInt(idx++, tokenUserId.intValue());
            } else {
                stmt.setNull(idx++, Types.INTEGER);
            }

            if (roleId != null) {
                stmt.setInt(idx++, roleId.intValue());
            } else {
                stmt.setNull(idx++, Types.INTEGER);
            }

            if (schoolId != null) {
                stmt.setInt(idx++, schoolId.intValue());
            } else {
                stmt.setNull(idx++, Types.INTEGER);
            }

            stmt.setString(idx++, moduleKey);
            stmt.setString(idx, lang);

            boolean hasResultSet = stmt.execute();
            if (hasResultSet) {
                try (ResultSet rs = stmt.getResultSet()) {
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    while (rs.next()) {
                        Map<String, Object> row = new LinkedHashMap<>();
                        for (int c = 1; c <= columnCount; c++) {
                            row.put(metaData.getColumnLabel(c), rs.getObject(c));
                        }
                        permissions.add(row);
                    }
                }
            }
        }

        return permissions;
    }

    public Map<String, Object> createPermission(Long tokenUserId, Object payload, String lang) throws Exception {
        String call = "{CALL createPermission(?,?,?)}";
        Map<String, Object> response = new LinkedHashMap<>();
        String payloadJson = objectMapper.writeValueAsString(payload);

        try (Connection conn = dataSource.getConnection(); CallableStatement stmt = conn.prepareCall(call)) {
            if (tokenUserId != null) {
                stmt.setInt(1, tokenUserId.intValue());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }

            stmt.setString(2, payloadJson);
            stmt.setString(3, lang);

            boolean hasResultSet = stmt.execute();
            if (hasResultSet) {
                try (ResultSet rs = stmt.getResultSet()) {
                    if (rs.next()) {
                        String jsonResponse = rs.getString(1);
                        response = objectMapper.readValue(jsonResponse, Map.class);
                    }
                }
            }
        }

        return response;
    }

    public Map<String, Object> updatePermission(Long tokenUserId, Long permissionId, Object payload, String lang)
            throws Exception {
        String call = "{CALL updatePermission(?,?,?,?)}";
        Map<String, Object> response = new LinkedHashMap<>();
        String payloadJson = objectMapper.writeValueAsString(payload);

        try (Connection conn = dataSource.getConnection(); CallableStatement stmt = conn.prepareCall(call)) {
            if (tokenUserId != null) {
                stmt.setInt(1, tokenUserId.intValue());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }

            if (permissionId != null) {
                stmt.setInt(2, permissionId.intValue());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            stmt.setString(3, payloadJson);
            stmt.setString(4, lang);

            boolean hasResultSet = stmt.execute();
            if (hasResultSet) {
                try (ResultSet rs = stmt.getResultSet()) {
                    if (rs.next()) {
                        String jsonResponse = rs.getString(1);
                        response = objectMapper.readValue(jsonResponse, Map.class);
                    }
                }
            }
        }

        return response;
    }
}
