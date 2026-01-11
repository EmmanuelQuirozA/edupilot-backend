package com.monarchsolutions.sms.repository.catalogs;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentConceptsProcedureRepository {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ObjectMapper objectMapper;

    public Map<String, Object> createPaymentConcept(Long tokenUserId, Object payload, String lang) throws Exception {
        String call = "{CALL createPaymentConcept(?,?,?)}";
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

    public Map<String, Object> updatePaymentConcept(Long tokenUserId, Long paymentConceptId, Object payload, String lang)
            throws Exception {
        String call = "{CALL updatePaymentConcept(?,?,?,?)}";
        Map<String, Object> response = new LinkedHashMap<>();
        String payloadJson = objectMapper.writeValueAsString(payload);

        try (Connection conn = dataSource.getConnection(); CallableStatement stmt = conn.prepareCall(call)) {
            if (tokenUserId != null) {
                stmt.setInt(1, tokenUserId.intValue());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }

            if (paymentConceptId != null) {
                stmt.setInt(2, paymentConceptId.intValue());
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
