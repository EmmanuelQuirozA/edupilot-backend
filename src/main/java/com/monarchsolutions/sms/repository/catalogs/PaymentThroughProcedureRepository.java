package com.monarchsolutions.sms.repository.catalogs;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentThroughProcedureRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ObjectMapper objectMapper;

    public Map<String, Object> createPaymentThrough(Long tokenUserId, Object payload, String lang) throws Exception {
        String payloadJson = objectMapper.writeValueAsString(payload);

        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("createPaymentThrough");
        query.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(2, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(3, String.class, ParameterMode.IN);

        query.setParameter(1, tokenUserId != null ? tokenUserId.intValue() : null);
        query.setParameter(2, payloadJson);
        query.setParameter(3, lang);

        query.execute();
        return readResponse(query.getResultList());
    }

    public Map<String, Object> updatePaymentThrough(Long tokenUserId, Long paymentThroughId, Object payload, String lang)
            throws Exception {
        String payloadJson = objectMapper.writeValueAsString(payload);

        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("updatePaymentThrough");
        query.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(2, Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(3, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(4, String.class, ParameterMode.IN);

        query.setParameter(1, tokenUserId != null ? tokenUserId.intValue() : null);
        query.setParameter(2, paymentThroughId != null ? paymentThroughId.intValue() : null);
        query.setParameter(3, payloadJson);
        query.setParameter(4, lang);

        query.execute();
        return readResponse(query.getResultList());
    }

    private Map<String, Object> readResponse(List<?> results) throws Exception {
        Map<String, Object> response = new LinkedHashMap<>();
        if (results == null || results.isEmpty()) {
            return response;
        }

        Object last = results.get(results.size() - 1);
        Object payload = unwrapPayload(last);
        if (payload == null) {
            return response;
        }

        if (payload instanceof Map<?, ?> mapPayload) {
            response.putAll((Map<String, Object>) mapPayload);
            return response;
        }

        String jsonResponse = payload.toString();
        if (jsonResponse == null || jsonResponse.isBlank()) {
            return response;
        }

        return objectMapper.readValue(jsonResponse, Map.class);
    }

    private Object unwrapPayload(Object payload) {
        if (payload instanceof Object[] arrayPayload) {
            if (arrayPayload.length == 0) {
                return null;
            }
            return arrayPayload[0];
        }
        return payload;
    }
}
