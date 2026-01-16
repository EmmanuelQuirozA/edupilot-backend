package com.monarchsolutions.sms.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RoleProcedureRepository {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ObjectMapper objectMapper;

    public Map<String, Object> createRole(Long tokenUserId, Object payload, String lang) throws Exception {
        String call = "{CALL createRole(?,?,?)}";
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

            response = readResponse(stmt);
        }

        return response;
    }

    public Map<String, Object> updateRole(Long tokenUserId, Long roleId, Object payload, String lang) throws Exception {
        String call = "{CALL updateRole(?,?,?,?)}";
        Map<String, Object> response = new LinkedHashMap<>();
        String payloadJson = objectMapper.writeValueAsString(payload);

        try (Connection conn = dataSource.getConnection(); CallableStatement stmt = conn.prepareCall(call)) {
            if (tokenUserId != null) {
                stmt.setInt(1, tokenUserId.intValue());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }

            if (roleId != null) {
                stmt.setInt(2, roleId.intValue());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            stmt.setString(3, payloadJson);
            stmt.setString(4, lang);

            response = readResponse(stmt);
        }

        return response;
    }

    private Map<String, Object> readResponse(CallableStatement stmt) throws SQLException {
        Map<String, Object> response = new LinkedHashMap<>();

        boolean hasResultSet = stmt.execute();
        if (!hasResultSet) {
            return response;
        }

        try (ResultSet rs = stmt.getResultSet()) {
            if (!rs.next()) {
                return response;
            }

            String jsonResponse = rs.getString(1);
            if (jsonResponse == null || jsonResponse.isBlank()) {
                return response;
            }

            response = objectMapper.readValue(jsonResponse, Map.class);
        } catch (Exception ex) {
            throw new SQLException("Failed to parse stored procedure response", ex);
        }

        return response;
    }
}
