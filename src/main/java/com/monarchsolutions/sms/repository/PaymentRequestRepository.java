package com.monarchsolutions.sms.repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monarchsolutions.sms.dto.paymentRequests.CreatePaymentRequestDTO;
import com.monarchsolutions.sms.dto.paymentRequests.CreatePaymentRecurrenceDTO;
import com.monarchsolutions.sms.dto.paymentRequests.CreatePaymentRequestScheduleDTO;
import com.monarchsolutions.sms.dto.paymentRequests.StudentPaymentRequestDTO;
import com.monarchsolutions.sms.dto.paymentRequests.ValidatePaymentRequestExistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.transaction.Transactional;

@Repository
public class PaymentRequestRepository {
    
  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private ObjectMapper objectMapper;

  // Create PaymentRequests
  public Map<String, Object> createPaymentRequest(Long token_user_id,
                                                  Long school_id,
                                                  Long group_id,
                                                  Long student_id,
                                                  CreatePaymentRequestDTO request,
                                                  String lang) throws Exception {
    // 0) if payment_month is the empty string, force it to null:
    if ("".equals(request.getPayment_month())) {
        request.setPayment_month(null);
    }

    // Convert the request DTO to a JSON string
    String payloadDataJson = objectMapper.writeValueAsString(request);

    // Create the stored procedure query
    StoredProcedureQuery query = entityManager.createStoredProcedureQuery("createPaymentRequests");

    // 1) Register IN params exactly as your SP signature expects:
    query.registerStoredProcedureParameter("token_user_id", Long.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("p_school_id", Long.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("p_group_id", Long.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("p_student_id", Long.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("p_payload", String.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);

    // 2) Set each parameter value
    query.setParameter("token_user_id", token_user_id != null ? token_user_id.intValue() : null);
    query.setParameter("p_school_id", school_id != null ? school_id.intValue() : null);
    query.setParameter("p_group_id", group_id != null ? group_id.intValue() : null);
    query.setParameter("p_student_id", student_id != null ? student_id.intValue() : null);
    query.setParameter("p_payload", payloadDataJson);
    query.setParameter("lang", lang);

    // 3) Execute. Because INSERT generates an “update count,” JPA sees multiple results.
    query.execute();

    // 4) Retrieve the JSON response from the stored procedure.
    List<Object> raw = collectStoredProcedureResults(query);
    if (raw.isEmpty()) {
      return Collections.emptyMap();
    }

    return mergeCreatePaymentRequestResult(raw);
  }

  public Map<String, Object> createPaymentRequestSchedule(Long tokenUserId,
                                                          Long schoolId,
                                                          Long groupId,
                                                          Long studentId,
                                                          CreatePaymentRequestScheduleDTO request,
                                                          String lang) throws Exception {
    String payloadDataJson = objectMapper.writeValueAsString(request);

    StoredProcedureQuery query = entityManager.createStoredProcedureQuery("createPaymentRequestSchedule");

    query.registerStoredProcedureParameter("token_user_id", Long.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("p_school_id", Long.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("p_group_id", Long.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("p_student_id", Long.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("p_payload", String.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);

    query.setParameter("token_user_id", tokenUserId != null ? tokenUserId.intValue() : null);
    query.setParameter("p_school_id", schoolId != null ? schoolId.intValue() : null);
    query.setParameter("p_group_id", groupId != null ? groupId.intValue() : null);
    query.setParameter("p_student_id", studentId != null ? studentId.intValue() : null);
    query.setParameter("p_payload", payloadDataJson);
    query.setParameter("lang", lang);

    query.execute();

    List<Object> raw = collectStoredProcedureResults(query);
    if (raw.isEmpty()) {
      return Collections.emptyMap();
    }

    return mergeCreatePaymentRequestResult(raw);
  }

  /**
   * Calls the createPaymentRecurrence stored procedure and returns its JSON result.
   */
  public String createPaymentRecurrence(Long tokenUserId,
                                        CreatePaymentRecurrenceDTO dto,
                                        String lang) {
    StoredProcedureQuery q = entityManager
        .createStoredProcedureQuery("createPaymentRecurrence")
        .registerStoredProcedureParameter("p_token_user_id", Integer.class, ParameterMode.IN)
        .registerStoredProcedureParameter("p_school_id", Integer.class, ParameterMode.IN)
        .registerStoredProcedureParameter("p_group_id", Integer.class, ParameterMode.IN)
        .registerStoredProcedureParameter("p_student_id", Integer.class, ParameterMode.IN)
        .registerStoredProcedureParameter("p_payment_concept_id", Integer.class, ParameterMode.IN)
        .registerStoredProcedureParameter("p_amount", java.math.BigDecimal.class, ParameterMode.IN)
        .registerStoredProcedureParameter("p_fee_type", String.class, ParameterMode.IN)
        .registerStoredProcedureParameter("p_late_fee", java.math.BigDecimal.class, ParameterMode.IN)
        .registerStoredProcedureParameter("p_late_fee_frequency", Integer.class, ParameterMode.IN)
        .registerStoredProcedureParameter("p_period", String.class, ParameterMode.IN)
        .registerStoredProcedureParameter("p_interval_count", Integer.class, ParameterMode.IN)
        .registerStoredProcedureParameter("p_start_date", java.sql.Date.class, ParameterMode.IN)
        .registerStoredProcedureParameter("p_end_date", java.sql.Date.class, ParameterMode.IN)
        .registerStoredProcedureParameter("p_comments", String.class, ParameterMode.IN)
        .registerStoredProcedureParameter("p_payment_month", java.sql.Date.class, ParameterMode.IN)
        .registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);

    q.setParameter("p_token_user_id", tokenUserId != null ? tokenUserId.intValue() : null);
    q.setParameter("p_school_id", dto.getSchool_id());
    q.setParameter("p_group_id", dto.getGroup_id());
    q.setParameter("p_student_id", dto.getStudent_id());
    q.setParameter("p_payment_concept_id", dto.getPayment_concept_id());
    q.setParameter("p_amount", dto.getAmount());
    q.setParameter("p_fee_type", dto.getFee_type());
    q.setParameter("p_late_fee", dto.getLate_fee());
    q.setParameter("p_late_fee_frequency", dto.getLate_fee_frequency());
    q.setParameter("p_period", dto.getPeriod());
    q.setParameter("p_interval_count", dto.getInterval_count());
    q.setParameter("p_start_date", dto.getStart_date() != null ? java.sql.Date.valueOf(dto.getStart_date()) : null);
    q.setParameter("p_end_date", dto.getEnd_date() != null ? java.sql.Date.valueOf(dto.getEnd_date()) : null);
    q.setParameter("p_comments", dto.getComments());
    q.setParameter("p_payment_month", dto.getPayment_month() != null ? java.sql.Date.valueOf(dto.getPayment_month()) : null);
    q.setParameter("lang", lang);

    q.execute();
    Object single = q.getSingleResult();
    if (single instanceof Object[]) {
      return (String) ((Object[]) single)[0];
    }
    return (String) single;
  }

  // Get payment request Activity Logs List
	public List<ValidatePaymentRequestExistence> validatePaymentRequests(Long token_user_id, Long school_id, Long group_id, Long payment_concept_id, Date payment_month){
		// Create the stored procedure query
		StoredProcedureQuery query = entityManager.createStoredProcedureQuery("validatePaymentRequests");

		// Register IN parameters
    query.registerStoredProcedureParameter("token_user_id", Long.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("school_id", Long.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("group_id", Long.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("payment_concept_id", Long.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("payment_month", java.sql.Date.class, ParameterMode.IN);

		// Set the parameter values
    query.setParameter("token_user_id", token_user_id != null ? token_user_id.intValue() : null);
    query.setParameter("school_id", school_id != null ? school_id.intValue() : null);
    query.setParameter("group_id", group_id != null ? group_id.intValue() : null);
    query.setParameter("payment_concept_id", payment_concept_id != null ? payment_concept_id.intValue() : null);
    query.setParameter("payment_month", payment_month != null ? payment_month.toLocalDate() : null);

		// Execute the stored procedure
		query.execute();

		// Retrieve the results as a list of Object arrays
		List<?> raw = query.getResultList();

		// if SP returned a JSON string (e.g. error / “no rows”) we’ll see a String here
    if (raw.isEmpty()) {
			return Collections.emptyList();
		}
    // if the SP returned an error-json, it'll be a single-element List<String>
    if (raw.size() == 1 && raw.get(0) instanceof String) {
      return Collections.emptyList();
    }

		@SuppressWarnings("unchecked")
		List<Object[]> rows = (List<Object[]>) raw;
		List<ValidatePaymentRequestExistence> logs = new ArrayList<>(rows.size());
		for (Object[] data : rows) {
			logs.add(mapRows(data));
		}
		return logs;
	}

	private ValidatePaymentRequestExistence mapRows(Object[] data) {
		ValidatePaymentRequestExistence dto = new ValidatePaymentRequestExistence();
		dto.setStudent_id(data[0] != null ? ((Number) data[0]).longValue() : null);
		dto.setFull_name(data[1] != null ? ((String) data[1]) : null);
		return dto;
	}


  public BigDecimal getPendingByStudent(Long token_user_id, Long studentId) {
    if (studentId!=null) {
      var sql = """
        SELECT 
        IFNULL(SUM(pr.amount),0) AS pending_total
        FROM payment_requests pr
        JOIN students st 
          ON pr.student_id = st.student_id
        -- get the student's user & school
        JOIN users u_st 
          ON st.user_id = u_st.user_id
        -- get the caller's user, role & school
        JOIN users u_call 
          ON u_call.user_id = :token_user_id
        JOIN roles r_call 
          ON u_call.role_id = r_call.role_id
        -- find if caller's school is related to the student's school
        LEFT JOIN schools s_rel 
          ON s_rel.related_school_id = u_call.school_id
        AND s_rel.school_id         = u_st.school_id
        WHERE pr.payment_status_id NOT IN (3,4,7,8)
          AND pr.student_id          = :studentId
          AND (
              -- STUDENT may only see their own balance
              ( r_call.name_en = 'Student' 
                AND u_call.user_id = u_st.user_id
              )
              OR
              -- OTHERS must share the same school or be in a related school
              ( r_call.name_en <> 'Student'
                AND ( u_call.school_id = u_st.school_id
                    OR s_rel.related_school_id IS NOT NULL
                    )
              )
          );
        """;

      Object single = entityManager
        .createNativeQuery(sql)
        .setParameter("studentId", studentId)
        .setParameter("token_user_id", token_user_id)
        .getSingleResult();

      if (single == null) {
        return BigDecimal.ZERO;
      }
      // MySQL may return BigDecimal or BigInteger
      if (single instanceof Number n) {
        return new BigDecimal(n.toString());
      }
      throw new IllegalStateException("Unexpected type for sum: " + single.getClass());
    } else {
      var sql = """
        SELECT 
        IFNULL(SUM(pr.amount),0) AS pending_total
        FROM payment_requests pr
        JOIN students st 
          ON pr.student_id = st.student_id
        -- get the student's user & school
        JOIN users u_st 
          ON st.user_id = u_st.user_id
        -- get the caller's user, role & school
        JOIN users u_call 
          ON u_call.user_id = :token_user_id
        JOIN roles r_call 
          ON u_call.role_id = r_call.role_id
        -- find if caller's school is related to the student's school
        LEFT JOIN schools s_rel 
          ON s_rel.related_school_id = u_call.school_id
        AND s_rel.school_id         = u_st.school_id
        WHERE pr.payment_status_id NOT IN (3,4,7,8)
          AND (
              -- STUDENT may only see their own balance
              ( r_call.name_en = 'Student' 
                AND u_call.user_id = u_st.user_id
              )
              OR
              -- OTHERS must share the same school or be in a related school
              ( r_call.name_en <> 'Student'
                AND ( u_call.school_id = u_st.school_id
                    OR s_rel.related_school_id IS NOT NULL
                    )
              )
          );
        """;

      Object single = entityManager
        .createNativeQuery(sql)
        .setParameter("token_user_id", token_user_id)
        .getSingleResult();

      if (single == null) {
        return BigDecimal.ZERO;
      }
      // MySQL may return BigDecimal or BigInteger
      if (single instanceof Number n) {
        return new BigDecimal(n.toString());
      }
      throw new IllegalStateException("Unexpected type for sum: " + single.getClass());

    }
  }
    
  @Transactional(Transactional.TxType.REQUIRED)
  public List<StudentPaymentRequestDTO> getStudentPaymentRequests(
      Long studentId,
      String  lang
  ) {
    StoredProcedureQuery q =
      entityManager.createStoredProcedureQuery("getStudentPaymentRequests");

    q.registerStoredProcedureParameter("p_student_id", Long.class,   ParameterMode.IN);
    q.registerStoredProcedureParameter("lang",         String.class,    ParameterMode.IN);

    q.setParameter("p_student_id", studentId);
    q.setParameter("lang",         lang);

    q.execute();

    @SuppressWarnings("unchecked")
    List<Object[]> rows = q.getResultList();
    List<StudentPaymentRequestDTO> out = new ArrayList<>(rows.size());

    for (Object[] r : rows) {
      StudentPaymentRequestDTO dto = new StudentPaymentRequestDTO();

      dto.setPaymentRequestId(
        r[0] != null ? ((Number) r[0]).intValue() : null
      );
      dto.setPaymentReference(
        r[1] != null ? r[1].toString() : null
      );
      dto.setStudentFullName(
        r[2] != null ? r[2].toString() : null
      );
      dto.setGeneration(
        r[3] != null ? r[3].toString() : null
      );
      dto.setScholarLevelName(
        r[4] != null ? r[4].toString() : null
      );
      dto.setGradeGroup(
        r[5] != null ? r[5].toString() : null
      );
      dto.setPrAmount(
        (BigDecimal) r[6]
      );
      // pr_created_at → LocalDateTime
      if (r[7] instanceof Timestamp ts) {
        dto.setPrCreatedAt(ts.toLocalDateTime());
      }
      // pr_pay_by → LocalDate
      if (r[8] instanceof Timestamp d) {
        dto.setPrPayBy(d.toLocalDateTime());
      }
      dto.setLateFee(
        r[9] != null ? (BigDecimal) r[9] : null
      );
      dto.setFeeType(
        r[10] != null ? r[10].toString() : null
      );
      dto.setLateFeeFrequency(
        r[11] != null ? ((Number) r[11]).intValue() : null
      );
      // payment_month → LocalDate
      if (r[12] instanceof Date pm) {
        dto.setPaymentMonth(pm.toLocalDate());
      }
      dto.setStudentId(
        r[13] != null ? ((Number) r[13]).intValue() : null
      );
      dto.setPaymentStatusId(
        r[14] != null ? ((Number) r[14]).intValue() : null
      );
      dto.setPsPrName(
        r[15] != null ? r[15].toString() : null
      );
      dto.setPtName(
        r[16] != null ? r[16].toString() : null
      );
      dto.setTotalAmountPayments(
        r[17] != null ? (BigDecimal) r[17] : null
      );
      if (r[18] instanceof Timestamp lt) {
        dto.setLatestPaymentDate(lt.toLocalDateTime());
      }
      dto.setLateFeeTotal(
        r[19] != null ? (BigDecimal) r[19] : null
      );

      out.add(dto);
    }
    return out;
  }

  private static final java.util.Set<String> META_KEYS = java.util.Set.of(
      "type",
      "title",
      "message",
      "success",
      "code",
      "status",
      "errors"
  );

  private Map<String, Object> mergeCreatePaymentRequestResult(List<?> raw) throws Exception {
    if (raw == null || raw.isEmpty()) {
      return Collections.emptyMap();
    }

    Map<String, Object> response = new java.util.LinkedHashMap<>();
    Map<String, Object> dataSection = new java.util.LinkedHashMap<>();

    java.util.List<Map<String, Object>> firstRows = convertRowToMaps(raw.get(0));
    for (Map<String, Object> row : firstRows) {
      mergeRowIntoResponse(row, response, dataSection);
    }

    java.util.List<String> listKeys = dataSection.entrySet().stream()
        .filter(e -> e.getValue() instanceof java.util.List<?> )
        .map(Map.Entry::getKey)
        .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));

    int listKeyIndex = 0;
    for (int i = 1; i < raw.size(); i++) {
      java.util.List<Map<String, Object>> rows = convertRowToMaps(raw.get(i));
      if (rows.isEmpty()) {
        continue;
      }

      String targetKey = null;
      if (listKeyIndex < listKeys.size()) {
        targetKey = listKeys.get(listKeyIndex++);
      } else if (dataSection.containsKey("created") && dataSection.get("created") instanceof java.util.List<?>) {
        targetKey = "created";
      }

      if (targetKey != null) {
        appendRows(dataSection, targetKey, rows);
      } else {
        appendRows(dataSection, "rows", rows);
      }
    }

    if (!dataSection.isEmpty()) {
      response.put("data", dataSection);
    } else if (!response.containsKey("data")) {
      response.put("data", Collections.emptyMap());
    }

    return response;
  }

  private List<Object> collectStoredProcedureResults(StoredProcedureQuery query) {
    List<Object> allResults = new java.util.ArrayList<>();

    boolean moreResults = true;
    while (moreResults) {
      try {
        List<?> current = query.getResultList();
        if (current != null && !current.isEmpty()) {
          allResults.addAll(current);
        }
      } catch (IllegalStateException ignored) {
        query.getUpdateCount();
      }

      moreResults = query.hasMoreResults();
    }

    return allResults;
  }

  private Map<String, Object> extractMetaPortion(Map<String, Object> source) {
    Map<String, Object> meta = new java.util.LinkedHashMap<>();
    java.util.Iterator<Map.Entry<String, Object>> iterator = source.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<String, Object> entry = iterator.next();
      if (META_KEYS.contains(entry.getKey())) {
        meta.put(entry.getKey(), entry.getValue());
        iterator.remove();
      }
    }
    return meta;
  }

  private void mergeRowIntoResponse(Map<String, Object> row,
                                    Map<String, Object> response,
                                    Map<String, Object> dataSection) throws Exception {
    if (row == null || row.isEmpty()) {
      return;
    }

    Object nestedData = row.remove("data");
    if (nestedData != null) {
      mergeIntoDataSection(nestedData, dataSection);
    }

    Map<String, Object> meta = extractMetaPortion(row);
    if (!meta.isEmpty()) {
      response.putAll(meta);
    }

    if (!row.isEmpty()) {
      mergeIntoDataSection(row, dataSection);
    }
  }

  private void mergeIntoDataSection(Object source,
                                    Map<String, Object> target) throws Exception {
    if (source == null) {
      return;
    }

    if (source instanceof Map<?, ?> mapSource) {
      Map<String, Object> normalized = normalizeMap(mapSource);
      for (Map.Entry<String, Object> entry : normalized.entrySet()) {
        String key = entry.getKey();
        Object value = entry.getValue();
        if ("data".equals(key)) {
          mergeIntoDataSection(value, target);
          continue;
        }

        if (value instanceof Map<?, ?> nestedMap) {
          @SuppressWarnings("unchecked")
          Map<String, Object> existing = (Map<String, Object>) target.computeIfAbsent(
              key,
              k -> new java.util.LinkedHashMap<>()
          );
          mergeIntoDataSection(nestedMap, existing);
        } else if (value instanceof java.util.List<?> listValue) {
          appendRows(target, key, convertListToMapList(listValue));
        } else {
          target.put(key, value);
        }
      }
      return;
    }

    if (source instanceof java.util.List<?> list) {
      appendRows(target, "items", convertListToMapList(list));
    }
  }

  private java.util.List<Map<String, Object>> convertListToMapList(java.util.List<?> list) throws Exception {
    java.util.List<Map<String, Object>> rows = new java.util.ArrayList<>();
    for (Object item : list) {
      rows.addAll(convertRowToMaps(item));
    }
    return rows;
  }

  private void appendRows(Map<String, Object> target,
                          String key,
                          java.util.List<Map<String, Object>> rows) {
    if (rows == null || rows.isEmpty()) {
      return;
    }

    Object current = target.get(key);
    java.util.List<Map<String, Object>> list;
    if (current instanceof java.util.List<?>) {
      @SuppressWarnings("unchecked")
      java.util.List<Map<String, Object>> existing = (java.util.List<Map<String, Object>>) current;
      list = existing;
    } else {
      list = new java.util.ArrayList<>();
      target.put(key, list);
    }

    list.addAll(rows);
  }

  private java.util.List<Map<String, Object>> convertRowToMaps(Object raw) throws Exception {
    if (raw == null) {
      return java.util.List.of();
    }

    if (raw instanceof Map<?, ?> map) {
      return java.util.List.of(normalizeMap(map));
    }

    if (raw instanceof List<?> list) {
      java.util.List<Map<String, Object>> result = new java.util.ArrayList<>();
      for (Object item : list) {
        result.addAll(convertRowToMaps(item));
      }
      return result;
    }

    if (raw instanceof Object[] array) {
      if (array.length == 1) {
        return convertRowToMaps(array[0]);
      }
      Map<String, Object> map = new java.util.LinkedHashMap<>();
      for (int i = 0; i < array.length; i++) {
        map.put("column_" + i, array[i]);
      }
      return java.util.List.of(map);
    }

    if (raw instanceof String str) {
      String trimmed = str.trim();
      if (trimmed.isEmpty()) {
        return java.util.List.of();
      }
      if (trimmed.startsWith("{")) {
        @SuppressWarnings("unchecked")
        Map<String, Object> map = objectMapper.readValue(trimmed, Map.class);
        return java.util.List.of(normalizeMap(map));
      }
      if (trimmed.startsWith("[")) {
        @SuppressWarnings("unchecked")
        List<Object> list = objectMapper.readValue(trimmed, List.class);
        return convertRowToMaps(list);
      }

      Map<String, Object> map = new java.util.LinkedHashMap<>();
      map.put("value", trimmed);
      return java.util.List.of(map);
    }

    Map<String, Object> map = new java.util.LinkedHashMap<>();
    map.put("value", raw);
    return java.util.List.of(map);
  }

  private Map<String, Object> normalizeMap(Map<?, ?> original) {
    Map<String, Object> normalized = new java.util.LinkedHashMap<>();
    for (Map.Entry<?, ?> entry : original.entrySet()) {
      Object key = entry.getKey();
      if (key == null) {
        continue;
      }
      normalized.put(key.toString(), entry.getValue());
    }
    return normalized;
  }

}
