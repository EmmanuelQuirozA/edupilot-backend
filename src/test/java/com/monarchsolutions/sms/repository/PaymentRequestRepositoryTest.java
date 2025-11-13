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
        "data":{"mass_upload":true,"created_count":2,"duplicates_count":0,
        "created":[{"full_name":"EMILY","student_id":10,"register_id":20,"_payment_request_id":3},
                    {"full_name":"ASHLY","student_id":11,"register_id":21,"_payment_request_id":4}],
        "duplicates":[]}}
        """;

    List<Object> raw = List.of(new Object[] { json });

    @SuppressWarnings("unchecked")
    Map<String, Object> response = (Map<String, Object>) mergeMethod.invoke(repository, raw);

    assertEquals("success", response.get("type"));
    assertTrue(response.containsKey("data"));

    @SuppressWarnings("unchecked")
    Map<String, Object> data = (Map<String, Object>) response.get("data");
    assertEquals(true, data.get("mass_upload"));
    assertEquals(2, data.get("created_count"));

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> created = (List<Map<String, Object>>) data.get("created");
    assertEquals(2, created.size());
    assertEquals("EMILY", created.get(0).get("full_name"));
    assertEquals(10, created.get(0).get("student_id"));
    assertEquals(20, created.get(0).get("register_id"));
    assertEquals(3, created.get(0).get("_payment_request_id"));
  }

  @Test
  void mergeResultAppendsRowsFromAdditionalResultSets() throws Exception {
    String json = """
        {"type":"success","title":"created","message":"done","success":true,
        "data":{"created":[],"duplicates":[]}}
        """;

    List<Map<String, Object>> createdRows = new ArrayList<>();
    createdRows.add(Map.of("full_name", "EMILY", "_payment_request_id", 3));
    createdRows.add(Map.of("full_name", "ASHLY", "_payment_request_id", 4));

    List<Map<String, Object>> duplicateRows = new ArrayList<>();
    duplicateRows.add(Map.of("full_name", "PAUL", "_payment_request_id", 9));

    List<Object> raw = new ArrayList<>();
    raw.add(new Object[] { json });
    raw.add(createdRows);
    raw.add(duplicateRows);

    @SuppressWarnings("unchecked")
    Map<String, Object> response = (Map<String, Object>) mergeMethod.invoke(repository, raw);

    @SuppressWarnings("unchecked")
    Map<String, Object> data = (Map<String, Object>) response.get("data");
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> created = (List<Map<String, Object>>) data.get("created");
    assertFalse(created.isEmpty());
    assertEquals("EMILY", created.get(0).get("full_name"));
    assertEquals(4, created.get(1).get("_payment_request_id"));

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> duplicates = (List<Map<String, Object>>) data.get("duplicates");
    assertEquals(1, duplicates.size());
    assertEquals("PAUL", duplicates.get(0).get("full_name"));
  }
}

