package com.monarchsolutions.sms.repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monarchsolutions.sms.dto.paymentRequests.CreatePaymentRequestDTO;
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
  public List<Map<String,Object>> createPaymentRequest(Long token_user_id, Long school_id, Long group_id, Long student_id, CreatePaymentRequestDTO request) throws Exception {
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
    query.registerStoredProcedureParameter("school_id", Long.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("group_id", Long.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("student_id", Long.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("payload", String.class, ParameterMode.IN);

    // 2) Set each parameter value
    query.setParameter("token_user_id", token_user_id != null ? token_user_id.intValue() : null);
    query.setParameter("school_id", school_id != null ? school_id.intValue() : null);
    query.setParameter("group_id", group_id != null ? group_id.intValue() : null);
    query.setParameter("student_id", student_id != null ? student_id.intValue() : null);
    query.setParameter("payload", payloadDataJson);

    // 3) Execute. Because INSERT generates an “update count,” JPA sees multiple results.
    query.execute();
    
    // 4) Now grab the SELECT rows (the second resultset). JPA lets us call getResultList().
    List<Object[]> raw = query.getResultList();

    // 5) Map each Object[] → a Map with keys “payment_request_id” and “full_name”
    List<Map<String,Object>> result = new ArrayList<>();
    for (Object[] row : raw) {
      Map<String,Object> m = new LinkedHashMap<>();
      // note: MySQL returns numeric columns usually as BigInteger or BigDecimal,
      // so cast appropriately (often ((Number)row[0]).intValue())
      Number prId = (Number) row[0];
      String fullName = (String) row[1];
      m.put("payment_request_id", prId != null ? prId.longValue() : null);
      m.put("full_name", fullName);
      result.add(m);
    }

    return result;
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
}
