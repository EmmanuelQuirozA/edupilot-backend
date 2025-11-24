package com.monarchsolutions.sms.repository;

import java.sql.Date;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monarchsolutions.sms.dto.common.PageResult;
import com.monarchsolutions.sms.dto.payments.StudentPaymentsDTO;
import com.monarchsolutions.sms.dto.payments.UpdatePaymentDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import java.util.*;

import javax.sql.DataSource;

import java.math.BigDecimal;
import java.sql.*;

@Repository
public class PaymentRepository {

	@Autowired
	private DataSource dataSource;

  @PersistenceContext
  private EntityManager em;
    
  @PersistenceContext
  private EntityManager entityManager;
    
  @Autowired
  private ObjectMapper objectMapper;

  public PageResult<Map<String,Object>> getPayments(
		Long tokenUserId,
		Long schoolId,
		Long studentId,
		Long paymentId,
		Long paymentRequestId,
		String studentFullName,
		String paymentReference,
		String generation,
		String gradeGroup,
		String ptName,
		String scholarLevelName,
		LocalDate paymentMonth,
		Date paymentCreatedAt,
		String lang,
		int page,
		int size,
		boolean exportAll,
		String order_by,
		String order_dir
	) throws SQLException {
		String call = "{CALL getPayments(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
		List<Map<String,Object>> content = new ArrayList<>();
		long totalCount = 0;

		try (Connection conn = dataSource.getConnection();
			CallableStatement stmt = conn.prepareCall(call)) {

			int idx = 1;
			// 1) the four IDs
			if (tokenUserId != null) { stmt.setInt(idx++, tokenUserId.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }
			if (schoolId != null) { stmt.setInt(idx++, schoolId.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }
			if (studentId != null) { stmt.setInt(idx++, studentId.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }
			if (paymentId != null) { stmt.setInt(idx++, paymentId.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }
			if (paymentRequestId != null) { stmt.setInt(idx++, paymentRequestId.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }

			// 2) the filters
			stmt.setString(idx++, studentFullName);
			stmt.setString(idx++, paymentReference);
			stmt.setString(idx++, generation);
			stmt.setString(idx++, gradeGroup);
			stmt.setString(idx++, ptName);
			stmt.setString(idx++, scholarLevelName);

			if (paymentMonth != null) { stmt.setDate(idx++, java.sql.Date.valueOf(paymentMonth)); } else { stmt.setNull(idx++, Types.DATE); }				
			// if (paymentMonth != null) { stmt.setDate(idx++, paymentMonth); } else { stmt.setNull(idx++, Types.DATE); }
			if (paymentCreatedAt != null) { stmt.setDate(idx++, paymentCreatedAt); } else { stmt.setNull(idx++, Types.DATE); }

			stmt.setString(idx++, lang);
			int offsetParam = page;     // rename 'page' var to 'offsetParam'
			int limitParam  = size;     // rename 'size' var to 'limitParam'
			// 15. offset
			if (exportAll) {
				stmt.setNull(idx++, Types.INTEGER);
				stmt.setNull(idx++, Types.INTEGER);
			} else {
				stmt.setInt(idx++, offsetParam);
				stmt.setInt(idx++, limitParam);
			}
			// 17. export_all
			stmt.setBoolean(idx++, exportAll);
			stmt.setString(idx++, order_by);
			stmt.setString(idx++, order_dir);
				
			// -- execute & read page result --
			boolean hasRs = stmt.execute();
			if (hasRs) {
				try (ResultSet rs = stmt.getResultSet()) {
					ResultSetMetaData md = rs.getMetaData();
					int cols = md.getColumnCount();
					while (rs.next()) {
						Map<String,Object> row = new LinkedHashMap<>();
						for (int c = 1; c <= cols; c++) {
							row.put(md.getColumnLabel(c), rs.getObject(c));
						}
						content.add(row);
					}
				}
			}

			// -- advance to the second resultset: total count --
			if (stmt.getMoreResults()) {
				try (ResultSet rs2 = stmt.getResultSet()) {
					if (rs2.next()) {
						totalCount = rs2.getLong(1);
					}
				}
			}
		}

		return new PageResult<>(content, totalCount, page, size);
	}


  /**
   * Calls the createPayment SP and returns its JSON_OBJECT(...) result.
   */
  public String createPayment(
    Long tokenUserId,
    com.monarchsolutions.sms.dto.payments.CreatePayment req,
    String lang
  ) {

    StoredProcedureQuery q = em
      .createStoredProcedureQuery("createPayment")
      .registerStoredProcedureParameter("p_token_user_id", Integer.class, ParameterMode.IN)
      .registerStoredProcedureParameter("p_student_id", Integer.class, ParameterMode.IN)
      .registerStoredProcedureParameter("p_payment_concept_id", Integer.class, ParameterMode.IN)
      .registerStoredProcedureParameter("p_payment_month", java.sql.Date.class, ParameterMode.IN)
      .registerStoredProcedureParameter("p_payment_date", java.sql.Date.class, ParameterMode.IN)
      .registerStoredProcedureParameter("p_amount", java.math.BigDecimal.class, ParameterMode.IN)
      .registerStoredProcedureParameter("p_comments", String.class, ParameterMode.IN)
      .registerStoredProcedureParameter("p_payment_request_id", Integer.class, ParameterMode.IN)
      .registerStoredProcedureParameter("p_payment_through_id", Integer.class, ParameterMode.IN)
      .registerStoredProcedureParameter("p_receipt_path", String.class, ParameterMode.IN)
      .registerStoredProcedureParameter("p_receipt_file_name", String.class, ParameterMode.IN)
      .registerStoredProcedureParameter("p_responsable_user_id", Integer.class, ParameterMode.IN)
      .registerStoredProcedureParameter("lang", String.class, ParameterMode.IN)
      // this SP always returns a single row with a single column "result" (the JSON)
    ;

    Integer tokenUserIdInt = tokenUserId != null ? tokenUserId.intValue() : null;
    Integer studentId = req.getStudent_id() != null ? req.getStudent_id().intValue() : null;
    Integer conceptId = req.getPayment_concept_id() != null ? req.getPayment_concept_id().intValue() : null;
    Integer paymentRequestId = req.getPayment_request_id() != null ? req.getPayment_request_id().intValue() : null;
    Integer paymentThroughId = req.getPayment_through_id() != null ? req.getPayment_through_id().intValue() : null;

    q.setParameter("p_token_user_id", tokenUserIdInt);
    q.setParameter("p_student_id", studentId);
    q.setParameter("p_payment_concept_id", conceptId);

    String pm = req.getPayment_month();
    Date sqlPm = null;
    if (pm != null && !pm.trim().isEmpty()) {
      sqlPm = Date.valueOf(pm);
    }
    q.setParameter("p_payment_month", sqlPm);

    String paymentDate = req.getPayment_date();
    Date sqlPaymentDate = null;
    if (paymentDate != null && !paymentDate.trim().isEmpty()) {
      sqlPaymentDate = Date.valueOf(paymentDate);
    }
    q.setParameter("p_payment_date", sqlPaymentDate);

    q.setParameter("p_amount", req.getAmount());
    q.setParameter("p_comments", req.getComments());
    q.setParameter("p_payment_request_id", paymentRequestId);
    q.setParameter("p_payment_through_id", paymentThroughId);
    q.setParameter("p_receipt_path", req.getReceipt_path());
    q.setParameter("p_receipt_file_name", req.getReceipt_file_name());
    // the one who’s performing the action is the same as token user
    q.setParameter("p_responsable_user_id", tokenUserIdInt);
    q.setParameter("lang", lang);

    q.execute();

    // getSingleResult() will be a single‐element Object[], or in some MySQL/JPA
    // configurations a single String if you SELECT JSON_OBJECT(...) as result
    Object single = q.getSingleResult();
    if (single instanceof Object[]) {
        // first element is the JSON text
        return (String) ((Object[]) single)[0];
    } else {
        return (String) single;
    }
  }

  
    
  public String updatePayment(Long responsibleUserId, Long payment_id, UpdatePaymentDTO request, Boolean removeReceipt, String lang) throws Exception {
    // Convert the request DTO to a JSON string
    String paymentDataJson = objectMapper.writeValueAsString(request);

    // Create the stored procedure query
    StoredProcedureQuery query = entityManager.createStoredProcedureQuery("updatePayment");

    // Register the stored procedure parameters
    query.registerStoredProcedureParameter("responsable_user_id", Integer.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("payment_id", Long.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("p_json", String.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("removeReceipt", Boolean.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);

    // Set the parameters.
    query.setParameter("responsable_user_id", responsibleUserId);
    query.setParameter("payment_id", payment_id);
    query.setParameter("p_json", paymentDataJson);
    query.setParameter("removeReceipt", removeReceipt);
    query.setParameter("lang", lang);

    query.execute();
    Object result = query.getSingleResult();
    return result != null ? result.toString() : null;
  }


	public List<StudentPaymentsDTO> getGroupedPayments(
		Long tokenUserId,
		Integer paymentId,
		Integer paymentRequestId,
		String  ptName,
		LocalDate paymentMonth,
		LocalDate paymentCreatedAt,
		Boolean tuitions,
		String  lang
	) {
    // Create the stored procedure query
    StoredProcedureQuery q = em.createStoredProcedureQuery("getStudentPayments");

    // 1) register IN params
    q.registerStoredProcedureParameter("p_token_user_id",       Integer.class, ParameterMode.IN);
    q.registerStoredProcedureParameter("p_payment_id",          Integer.class, ParameterMode.IN);
    q.registerStoredProcedureParameter("p_payment_request_id",  Integer.class, ParameterMode.IN);
    q.registerStoredProcedureParameter("p_pt_name",             String .class, ParameterMode.IN);
    q.registerStoredProcedureParameter("p_payment_month",       Date   .class, ParameterMode.IN);
    q.registerStoredProcedureParameter("p_payment_created_at",  Date   .class, ParameterMode.IN);
    q.registerStoredProcedureParameter("p_tuitions",            Boolean.class, ParameterMode.IN);
    q.registerStoredProcedureParameter("lang",                  String .class, ParameterMode.IN);

    // 2) set parameter values
    q.setParameter("p_token_user_id",      tokenUserId.intValue());
    q.setParameter("p_payment_id",         paymentId);
    q.setParameter("p_payment_request_id", paymentRequestId);
    q.setParameter("p_pt_name",            ptName);
    q.setParameter("p_payment_month",      paymentMonth != null ? java.sql.Date.valueOf(paymentMonth) : null);
    q.setParameter("p_payment_created_at", paymentCreatedAt != null ? java.sql.Date.valueOf(paymentCreatedAt) : null);
    q.setParameter("p_tuitions",           tuitions);
    q.setParameter("lang",                 lang);

    // 3) execute and grab the result set
    q.execute();
    @SuppressWarnings("unchecked")
		List<Object[]> rows = q.getResultList();

    // 4) map each row to your DTO
    List<StudentPaymentsDTO> out = new ArrayList<>(rows.size());
    for (Object[] r : rows) {
      StudentPaymentsDTO dto = new StudentPaymentsDTO();
			dto.setPaymentId(r[0] != null? ((Number) r[0]).intValue(): null);
			dto.setStudentId(r[1] != null? ((Number) r[1]).intValue(): null);
			dto.setSchoolId(r[2] != null? ((Number) r[2]).intValue(): null);

			Object pmObj = r[3];
			if (pmObj instanceof java.sql.Timestamp) {
				dto.setPaymentMonth(
					((java.sql.Timestamp) pmObj)
						.toLocalDateTime()
						.toLocalDate()
				);
			} else if (pmObj instanceof java.sql.Date) {
				dto.setPaymentMonth(
					((java.sql.Date) pmObj)
						.toLocalDate()
				);
			} else {
				dto.setPaymentMonth(null);
			}

			dto.setAmount( r[4] != null ? (BigDecimal) r[4] : null);
			dto.setPaymentStatusId( r[5] != null ? ((Number) r[5]).intValue() : null);
			dto.setPaymentThroughId( r[6] != null ? ((Number) r[6]).intValue() : null);
			dto.setPaymentConceptId( r[7] != null ? ((Number) r[7]).intValue() : null);

			Object vaObj = r[8];
			if (vaObj instanceof java.sql.Timestamp) {
				dto.setValidatedAt(
					((java.sql.Timestamp) vaObj).toLocalDateTime()
				);
			} else {
				dto.setValidatedAt(null);
			}

			Object pcObj = r[9];
			if (pcObj instanceof java.sql.Timestamp) {
				dto.setPaymentCreatedAt(
					((java.sql.Timestamp) pcObj).toLocalDateTime()
				);
			} else {
				dto.setPaymentCreatedAt(null);
			}

			Object uaObj = r[10];
			if (uaObj instanceof java.sql.Timestamp) {
				dto.setUpdatedAt(
					((java.sql.Timestamp) uaObj).toLocalDateTime()
				);
			} else {
				dto.setUpdatedAt(null);
			}

			dto.setComments( r[11] != null ? r[11].toString() : null);
			dto.setPartConceptName( r[12] != null ? r[12].toString() : null);
			dto.setPaymentThroughName( r[13] != null ? r[13].toString() : null);
			dto.setPaymentStatusName( r[14] != null ? r[14].toString() : null);
			dto.setValidatorFullName( r[15] != null ? r[15].toString() : null);
			dto.setPaymentRequestId( r[16] != null ? ((Number) r[16]).intValue() : null);
			dto.setReceiptPath( r[17] != null ? r[17].toString() : null);
			dto.setReceiptFileName( r[18] != null ? r[18].toString() : null);
      out.add(dto);
    }
    return out;
  }

}