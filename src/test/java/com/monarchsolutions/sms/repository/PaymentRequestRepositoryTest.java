package com.monarchsolutions.sms.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

class PaymentRequestRepositoryTest {

  private PaymentRequestRepository repository;
  private Method mergeMethod;

  @BeforeEach
  void setUp() throws Exception {
    repository = new PaymentRequestRepository();
    Field objectMapperField = PaymentRequestRepository.class.getDeclaredField("objectMapper");
    objectMapperField.setAccessible(true);
    objectMapperField.set(repository, new ObjectMapper());

    mergeMethod = PaymentRequestRepository.class
        .getDeclaredMethod("mergeCreatePaymentRequestResult", List.class);
    mergeMethod.setAccessible(true);
  }

  @Test
  void mergeResultIncludesStudentsFromJsonPayload() throws Exception {
    String json = """
        {"type":"success","title":"created","message":"done","success":true,
        "data":[{"full_name":"EMILY","payment_request_id":3},{"full_name":"ASHLY","payment_request_id":4}]}
        """;

    List<Object> raw = List.of(new Object[] { json });

    @SuppressWarnings("unchecked")
    Map<String, Object> response = (Map<String, Object>) mergeMethod.invoke(repository, raw);

    assertEquals("success", response.get("type"));
    assertTrue(response.containsKey("data"));

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
    assertEquals(2, data.size());
    assertEquals("EMILY", data.get(0).get("full_name"));
    assertEquals(3, data.get(0).get("payment_request_id"));
  }

  @Test
  void mergeResultAppendsRowsFromAdditionalResultSets() throws Exception {
    String json = """
        {"type":"success","title":"created","message":"done","success":true,"data":[]}
        """;

    List<Map<String, Object>> secondResultSet = new ArrayList<>();
    secondResultSet.add(Map.of("full_name", "EMILY", "payment_request_id", 3));
    secondResultSet.add(Map.of("full_name", "ASHLY", "payment_request_id", 4));

    List<Object> raw = new ArrayList<>();
    raw.add(new Object[] { json });
    raw.add(secondResultSet);

    @SuppressWarnings("unchecked")
    Map<String, Object> response = (Map<String, Object>) mergeMethod.invoke(repository, raw);

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
    assertFalse(data.isEmpty());
    assertEquals("EMILY", data.get(0).get("full_name"));
    assertEquals(4, data.get(1).get("payment_request_id"));
  }
}

