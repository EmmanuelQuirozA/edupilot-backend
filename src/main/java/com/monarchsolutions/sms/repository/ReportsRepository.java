package com.monarchsolutions.sms.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monarchsolutions.sms.dto.reports.PaymentRequestListResponse;
import com.monarchsolutions.sms.dto.reports.PaymentRequestDetailsResponseV2;
import com.monarchsolutions.sms.dto.common.PageResult;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.sql.Date;

@Repository
public class ReportsRepository {

	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@PersistenceContext
	private EntityManager entityManager;

	private LocalDate safeToLocalDate(java.sql.Date d) {
		return d == null ? null : d.toLocalDate();
	}

	public PageResult<Map<String,Object>> getPaymentRequests(
		Long token_user_id,
		Long student_id,
		Long school_id,
		Long payment_request_id,
		LocalDate pr_created_start,
		LocalDate pr_created_end,
		LocalDate pr_pay_by_start,
		LocalDate pr_pay_by_end,
		LocalDate payment_month,
		String ps_pr_name,
		String pt_name,
		String payment_reference,
		String student_full_name,
		Boolean sc_enabled,
		Boolean u_enabled,
		Boolean g_enabled,
		Integer pr_payment_status_id,
		String grade_group,
		String lang,
		String order_by,
		String order_dir,
		Integer page,
		Integer size,
		boolean export_all
	) throws SQLException {
		String call = "{CALL getPaymentRequests(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
		List<Map<String,Object>> content = new ArrayList<>();
		long totalCount = 0;

		try (Connection conn = dataSource.getConnection();
		CallableStatement stmt = conn.prepareCall(call)) {

			int idx = 1;
			if (token_user_id != null) { stmt.setInt(idx++, token_user_id.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }				
			if (school_id != null) { stmt.setInt(idx++, school_id.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }				
			if (student_id != null) { stmt.setInt(idx++, student_id.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }				
			if (payment_request_id != null) { stmt.setInt(idx++, payment_request_id.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }				
			
			if (pr_created_start != null) { stmt.setDate(idx++, java.sql.Date.valueOf(pr_created_start)); } else { stmt.setNull(idx++, Types.DATE); }				
			if (pr_created_end != null) { stmt.setDate(idx++, java.sql.Date.valueOf(pr_created_end)); } else { stmt.setNull(idx++, Types.DATE); }				
			if (pr_pay_by_start != null) { stmt.setDate(idx++, java.sql.Date.valueOf(pr_pay_by_start)); } else { stmt.setNull(idx++, Types.DATE); }				
			if (pr_pay_by_end != null) { stmt.setDate(idx++, java.sql.Date.valueOf(pr_pay_by_end)); } else { stmt.setNull(idx++, Types.DATE); }				
			if (payment_month != null) { stmt.setDate(idx++, java.sql.Date.valueOf(payment_month)); } else { stmt.setNull(idx++, Types.DATE); }				

			stmt.setString(idx++, ps_pr_name);
			stmt.setString(idx++, pt_name);
			stmt.setString(idx++, payment_reference);
			stmt.setString(idx++, student_full_name);

			if (sc_enabled != null) { stmt.setBoolean(idx++, sc_enabled); } else { stmt.setNull(idx++, Types.BOOLEAN); }				
			if (u_enabled != null) { stmt.setBoolean(idx++, u_enabled); } else { stmt.setNull(idx++, Types.BOOLEAN); }				
			if (g_enabled != null) { stmt.setBoolean(idx++, g_enabled); } else { stmt.setNull(idx++, Types.BOOLEAN); }				

			if (pr_payment_status_id != null) { stmt.setInt(idx++, pr_payment_status_id.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }				

			stmt.setString(idx++, grade_group);
			stmt.setString(idx++, lang);
			stmt.setString(idx++, order_by);
			stmt.setString(idx++, order_dir);

			int offsetParam = page;     // rename 'page' var to 'offsetParam'
			int limitParam  = size;     // rename 'size' var to 'limitParam'

			// 15. offset
			if (export_all) {
				stmt.setNull(idx++, Types.INTEGER);
				stmt.setNull(idx++, Types.INTEGER);
			} else {
				stmt.setInt(idx++, offsetParam);
				stmt.setInt(idx++, limitParam);
			}
			// 17. export_all
			stmt.setBoolean(idx++, export_all);
			
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
	
        public PageResult<Map<String,Object>> getPaymentsPivotReport(
                Long schoolId,
                Long studentId,
                LocalDate startDate,
                LocalDate endDate,
		Boolean groupStatus,
		Boolean userStatus,
		String studentFullName,
		String paymentReference,
		String generation,
		String gradeGroup,
		String scholarLevel,
		String lang,
		int page,
		int size,
		boolean exportAll,
		boolean showDebtOnly,
		String order_by,
		String order_dir
	) throws SQLException {
		String call = "{CALL getPaymentsPivotReport(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
		List<Map<String,Object>> content = new ArrayList<>();
		long totalCount = 0;

		try (Connection conn = dataSource.getConnection();
		CallableStatement stmt = conn.prepareCall(call)) {

			int idx = 1;
			if (schoolId != null) { stmt.setInt(idx++, schoolId.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }				
			if (studentId != null) { stmt.setInt(idx++, studentId.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }				
			if (startDate != null) { stmt.setDate(idx++, java.sql.Date.valueOf(startDate)); } else { stmt.setNull(idx++, Types.DATE); }				
			if (endDate != null) { stmt.setDate(idx++, java.sql.Date.valueOf(endDate)); } else { stmt.setNull(idx++, Types.DATE); }				
			if (groupStatus != null) { stmt.setBoolean(idx++, groupStatus); } else { stmt.setNull(idx++, Types.BOOLEAN); }				
			if (userStatus != null) { stmt.setBoolean(idx++, userStatus); } else { stmt.setNull(idx++, Types.BOOLEAN); }				
			stmt.setString(idx++, studentFullName);
			stmt.setString(idx++, paymentReference);
			stmt.setString(idx++, generation);
			stmt.setString(idx++, gradeGroup);
			stmt.setString(idx++, scholarLevel);
			stmt.setString(idx++, lang);

			// compute offset
			// int offset = page * size;
			// stmt.setInt(idx++, offset);
			// stmt.setInt(idx++, size);
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
			stmt.setBoolean(idx++, showDebtOnly);
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

        public PageResult<Map<String,Object>> getPaymentRecurrences(
                Long tokenUserId,
                Long schoolId,
                Long studentId,
                Long paymentRequestId,
                LocalDate paymentMonth,
                String ptName,
                String paymentReference,
                String studentFullName,
                Boolean scEnabled,
                Boolean uEnabled,
                Boolean gEnabled,
                String gradeGroup,
                String lang,
                String orderBy,
                String orderDir,
                Integer offset,
                Integer limit,
                boolean exportAll
        ) throws SQLException {
                String call = "{CALL getPaymentRecurrences(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
                List<Map<String,Object>> content = new ArrayList<>();
                long totalCount = 0;

                try (Connection conn = dataSource.getConnection();
                        CallableStatement stmt = conn.prepareCall(call)) {

                        int idx = 1;
                        if (tokenUserId != null) { stmt.setInt(idx++, tokenUserId.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }
                        if (schoolId != null) { stmt.setInt(idx++, schoolId.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }
                        if (studentId != null) { stmt.setInt(idx++, studentId.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }
                        if (paymentRequestId != null) { stmt.setInt(idx++, paymentRequestId.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }

                        if (paymentMonth != null) { stmt.setDate(idx++, java.sql.Date.valueOf(paymentMonth)); } else { stmt.setNull(idx++, Types.DATE); }

                        stmt.setString(idx++, ptName);
                        stmt.setString(idx++, paymentReference);
                        stmt.setString(idx++, studentFullName);

                        if (scEnabled != null) { stmt.setBoolean(idx++, scEnabled); } else { stmt.setNull(idx++, Types.BOOLEAN); }
                        if (uEnabled != null) { stmt.setBoolean(idx++, uEnabled); } else { stmt.setNull(idx++, Types.BOOLEAN); }
                        if (gEnabled != null) { stmt.setBoolean(idx++, gEnabled); } else { stmt.setNull(idx++, Types.BOOLEAN); }

                        stmt.setString(idx++, gradeGroup);
                        stmt.setString(idx++, lang);
                        stmt.setString(idx++, orderBy);
                        stmt.setString(idx++, orderDir);

                        if (exportAll) {
                                stmt.setNull(idx++, Types.INTEGER);
                                stmt.setNull(idx++, Types.INTEGER);
                        } else {
                                if (offset != null) { stmt.setInt(idx++, offset); } else { stmt.setNull(idx++, Types.INTEGER); }
                                if (limit != null) { stmt.setInt(idx++, limit); } else { stmt.setNull(idx++, Types.INTEGER); }
                        }

                        stmt.setBoolean(idx++, exportAll);

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

                        if (stmt.getMoreResults()) {
                                try (ResultSet rs2 = stmt.getResultSet()) {
                                        if (rs2.next()) {
                                                totalCount = rs2.getLong(1);
                                        }
                                }
                        }
                }

                int offsetValue = offset != null ? offset : 0;
                int limitValue  = limit != null ? limit : content.size();
                return new PageResult<>(content, totalCount, offsetValue, limitValue);
        }

        public PageResult<Map<String,Object>> getPaymentRequestSchedule(
                Long tokenUserId,
                String ruleName,
                Boolean active,
                Long schoolId,
                Long groupId,
                Long studentId,
                LocalDate dueStart,
                LocalDate dueEnd,
                String globalSearch,
                String orderBy,
                String orderDir,
                Integer offset,
                Integer limit,
                boolean exportAll,
                String lang
        ) throws SQLException {
                String call = "{CALL getPaymentRequestSchedule(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
                List<Map<String,Object>> content = new ArrayList<>();
                long totalCount = 0;

                try (Connection conn = dataSource.getConnection();
                        CallableStatement stmt = conn.prepareCall(call)) {

                        int idx = 1;
                        if (tokenUserId != null) { stmt.setInt(idx++, tokenUserId.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }
                        stmt.setString(idx++, ruleName);
                        if (active != null) { stmt.setBoolean(idx++, active); } else { stmt.setNull(idx++, Types.BOOLEAN); }
                        if (schoolId != null) { stmt.setInt(idx++, schoolId.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }
                        if (groupId != null) { stmt.setInt(idx++, groupId.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }
                        if (studentId != null) { stmt.setInt(idx++, studentId.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }

                        if (dueStart != null) { stmt.setDate(idx++, java.sql.Date.valueOf(dueStart)); } else { stmt.setNull(idx++, Types.DATE); }
                        if (dueEnd != null) { stmt.setDate(idx++, java.sql.Date.valueOf(dueEnd)); } else { stmt.setNull(idx++, Types.DATE); }

                        stmt.setString(idx++, globalSearch);
                        stmt.setString(idx++, orderBy);
                        stmt.setString(idx++, orderDir);

                        if (exportAll) {
                                stmt.setNull(idx++, Types.INTEGER);
                                stmt.setNull(idx++, Types.INTEGER);
                        } else {
                                if (offset != null) { stmt.setInt(idx++, offset); } else { stmt.setNull(idx++, Types.INTEGER); }
                                if (limit != null) { stmt.setInt(idx++, limit); } else { stmt.setNull(idx++, Types.INTEGER); }
                        }

                        stmt.setBoolean(idx++, exportAll);
                        stmt.setString(idx++, lang);

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

                        if (stmt.getMoreResults()) {
                                try (ResultSet rs2 = stmt.getResultSet()) {
                                        if (rs2.next()) {
                                                totalCount = rs2.getLong(1);
                                        }
                                }
                        }
                }

                int offsetValue = offset != null ? offset : 0;
                int limitValue  = limit != null ? limit : Math.max(content.size(), 1);
                return new PageResult<>(content, totalCount, offsetValue, limitValue);
        }

        public ReportsRepository(DataSource dataSource, ObjectMapper objectMapper) {
                this.dataSource     = dataSource;
                this.objectMapper   = objectMapper;
        }

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

	

	public PageResult<Map<String,Object>> getPaymentsStudents(
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
		String call = "{CALL getPaymentsStudent(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
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

	// Get Payment Request List
	public List<PaymentRequestListResponse> getPaymentRequests(Long tokenSchoolId, Long student_id, Long payment_id, String lang){
		// Create the stored procedure query
		StoredProcedureQuery query = entityManager.createStoredProcedureQuery("getPaymentRequests");

		// Register IN parameters
		query.registerStoredProcedureParameter("user_school_id", Long.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("student_id", Long.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("payment_id", Long.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);

		// Set the parameter values
		query.setParameter("user_school_id", tokenSchoolId);
		query.setParameter("student_id", student_id);
		query.setParameter("payment_id", payment_id);
		query.setParameter("lang", lang);

		// Execute the stored procedure
		query.execute();

		// Retrieve the results as a list of Object arrays
		@SuppressWarnings("unchecked")
		List<Object[]> results = query.getResultList();
		List<PaymentRequestListResponse> paymentRequests = new ArrayList<>();

		for (Object[] data : results) {
				paymentRequests.add(mapPaymentRequests(data));
		}
		return paymentRequests;
	}
	
	private PaymentRequestListResponse mapPaymentRequests(Object[] data) {
		MappingConfig[] config = new MappingConfig[] {
			new MappingConfig("payment_request_id", Long.class),
			new MappingConfig("payment_concept_id", Long.class),
			new MappingConfig("pr_amount", BigDecimal.class),
			new MappingConfig("pr_created_at", LocalDateTime.class),
			new MappingConfig("pr_pay_by", LocalDateTime.class),
			new MappingConfig("pr_comments", String.class),
			new MappingConfig("late_fee", BigDecimal.class),
			new MappingConfig("fee_type", String.class),
			new MappingConfig("late_fee_frequency", Integer.class),
			new MappingConfig("payment_month", Date.class),
			new MappingConfig("payment_status_id", Integer.class),
			new MappingConfig("ps_pr_name", String.class),
			new MappingConfig("pt_name", String.class),
			new MappingConfig("late_fee_total", BigDecimal.class),
			new MappingConfig("late_periods", Integer.class),
			new MappingConfig("to_pay", BigDecimal.class),
			new MappingConfig("pay_created_at", LocalDateTime.class),
			new MappingConfig("pay_amount", BigDecimal.class),
			new MappingConfig("ps_pay_name", String.class),
			new MappingConfig("student_id", Long.class),
			new MappingConfig("user_id", Long.class),
			new MappingConfig("payment_reference", String.class),
			new MappingConfig("generation", String.class),
			new MappingConfig("email", String.class),
			new MappingConfig("personal_email", String.class),
			new MappingConfig("student_full_name", String.class),
			new MappingConfig("phone_number", String.class),
			new MappingConfig("school_description", String.class),
			new MappingConfig("scholar_level_name", String.class),
			new MappingConfig("g_enabled", Boolean.class),
			new MappingConfig("u_enabled", Boolean.class),
			new MappingConfig("sc_enabled", Boolean.class),
			new MappingConfig("school_status", String.class),
			new MappingConfig("user_status", String.class),
			new MappingConfig("group_status", String.class),
			new MappingConfig("grade_group", String.class),
			new MappingConfig("validator_full_name", String.class),
			new MappingConfig("validator_phone_number", String.class),
			new MappingConfig("validator_username", String.class),
		};

		return MapperUtil.mapRow(data, config, PaymentRequestListResponse.class);
	}

	public PageResult<Map<String,Object>> getBalanceRecharges(
		Long token_user_id,
    Long user_id,
    Long school_id,
    String full_name,
    LocalDate created_at,
    String lang,
    int page,
    int size,
    Boolean exportAll,
    String order_by,
    String order_dir
	) throws SQLException {
		String call = "{CALL getBalanceRecharges(?,?,?,?,?,?,?,?,?,?,?)}";
		List<Map<String,Object>> content = new ArrayList<>();
		long totalCount = 0;

		try (Connection conn = dataSource.getConnection();
			CallableStatement stmt = conn.prepareCall(call)) {

			int idx = 1;
			// 1) the four IDs
			if (token_user_id != null) { stmt.setInt(idx++, token_user_id.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }
			if (user_id != null) { stmt.setInt(idx++, user_id.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }
			if (school_id != null) { stmt.setInt(idx++, school_id.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }

			// 2) the filters
			stmt.setString(idx++, full_name);
      // stmt.setDate(idx++, java.sql.Date.valueOf(created_at));
      if (created_at != null) {
          stmt.setDate(idx++, java.sql.Date.valueOf(created_at));
      } else {
          stmt.setNull(idx++, Types.DATE);
      }

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
			Boolean hasRs = stmt.execute();
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



	public PaymentRequestDetailsResponseV2.PaymentRequestDetailsResponse getPaymentRequestDetails(Long tokenUserId, Long schoolId, Long paymentRequestId, String lang) throws SQLException {
    var result = new PaymentRequestDetailsResponseV2.PaymentRequestDetailsResponse();
			// These lists will be filled from the three SELECTs:
			List<PaymentRequestDetailsResponseV2.StudentInfo> students = new ArrayList<>();
			List<PaymentRequestDetailsResponseV2.PaymentRequestInfo> requests = new ArrayList<>();
			List<PaymentRequestDetailsResponseV2.PaymentDetail> payments = new ArrayList<>();

			try(Connection conn = dataSource.getConnection();
					CallableStatement stmt = conn.prepareCall("{CALL getPaymentRequestDetails(?,?,?,?)}")) {

						stmt.setInt(1, tokenUserId.intValue());
						stmt.setInt(2, schoolId.intValue());
						stmt.setInt(3, paymentRequestId.intValue());
						stmt.setString(4, lang);

						// Execute and begin reading the first resultset
						boolean hasRs = stmt.execute();
				// 1st resultset → student row
				if (hasRs) {
					try(ResultSet rs = stmt.getResultSet()) {
						while(rs.next()) {
							var s = new PaymentRequestDetailsResponseV2.StudentInfo();
							s.setStudent_id(rs.getLong("student_id"));
							s.setUser_id(rs.getLong("user_id"));
							s.setPayment_reference(rs.getString("payment_reference"));
							s.setEmail(rs.getString("email"));
							s.setFull_name(rs.getString("student_full_name"));
							// s.setAddress(rs.getString("address"));
							s.setGeneration(rs.getString("generation"));
							s.setGrade_group(rs.getString("grade_group"));
							s.setScholar_level_name(rs.getString("scholar_level_name"));
							s.setPhone_number(rs.getString("phone_number"));
							students.add(s);
						}
					}
				}

				// 2) advance to the second RS
				if (stmt.getMoreResults()) {
					try(ResultSet rs = stmt.getResultSet()) {
						while(rs.next()) {
							var pr = new PaymentRequestDetailsResponseV2.PaymentRequestInfo();
							pr.setPayment_request_id(rs.getLong("payment_request_id"));
							pr.setPr_amount(rs.getBigDecimal("pr_amount"));
							pr.setPr_created_at(rs.getTimestamp("pr_created_at").toLocalDateTime());
							pr.setPr_pay_by(rs.getTimestamp("pr_pay_by").toLocalDateTime());
							pr.setPr_comments(rs.getString("pr_comments"));
							pr.setLate_fee(rs.getBigDecimal("late_fee"));
							pr.setFee_type(rs.getString("fee_type"));
							pr.setLate_fee_frequency(rs.getInt("late_fee_frequency"));
							pr.setPayment_month(safeToLocalDate(rs.getDate("payment_month")));
							pr.setPayment_status_id(rs.getObject("payment_status_id", Integer.class));
							pr.setPartial_payment(rs.getBoolean("partial_payment"));
							pr.setPartial_payment_transformed(rs.getString("partial_payment_transformed"));
							pr.setPs_pr_name(rs.getString("ps_pr_name"));
							pr.setPt_name(rs.getString("pt_name"));
							pr.setClosed_at(
								Optional.ofNullable(rs.getTimestamp("closed_at"))
									.map(Timestamp::toLocalDateTime)
									.orElse(null)
							);
							requests.add(pr);
						}
					}
				}

				// 3) advance to the third RS
				if (stmt.getMoreResults()) {
					try(ResultSet rs = stmt.getResultSet()) {
						while(rs.next()) {
						var pd = new PaymentRequestDetailsResponseV2.PaymentDetail();
						pd.setPayment_id(rs.getLong("payment_id"));
						Timestamp cre = rs.getTimestamp("created_at");
						if (cre != null) pd.setPay_created_at(cre.toLocalDateTime());
						Timestamp ppd = rs.getTimestamp("payment_date");
						if (ppd != null) pd.setPayment_date(ppd.toLocalDateTime());
						Timestamp upd = rs.getTimestamp("updated_at");
						if (upd != null) pd.setUpdated_at(upd.toLocalDateTime());
						Timestamp val = rs.getTimestamp("validated_at");
						if (val != null) pd.setValidated_at(val.toLocalDateTime());
						pd.setAmount(rs.getBigDecimal("amount"));
						pd.setPayment_status_id(rs.getLong("payment_status_id"));
						pd.setPayment_status_name(rs.getString("payment_status_name"));
						pd.setPt_name(rs.getString("pt_name"));
						pd.setValidator_username(rs.getString("validator_username"));
						pd.setValidator_full_name(rs.getString("validator_full_name"));
						pd.setValidator_phone_number(rs.getString("validator_phone_number"));
						payments.add(pd);
					}
				}
			}
		}

		// Your SP always returns exactly one student and one request,
		// so you can do:
		if (!students.isEmpty())      result.setStudent(students.get(0));
		if (!requests.isEmpty())      result.setPaymentRequest(requests.get(0));
		result.setPayments(payments);

		return result;
	}

			

	// Update Payment Request
	public String updatePaymentRequest(
		long paymentRequestId,
		long responsableUserId,
		Map<String,Object> jsonData,
		String lang
	) throws Exception {
		String call = "{ CALL updatePaymentRequest(?,?,?,?) }";
		try (
				Connection c = dataSource.getConnection();
				CallableStatement stmt = c.prepareCall(call)
		) {
			stmt.setInt(1, (int)paymentRequestId);
			stmt.setInt(2, (int)responsableUserId);
			// serialize JSON map to a MySQL JSON string
			String jsonText = objectMapper.writeValueAsString(jsonData);
			stmt.setString(3, jsonText);
			stmt.setString(4, lang);

			boolean gotRs = stmt.execute();
			if (gotRs) {
				try (ResultSet rs = stmt.getResultSet()) {
					if (rs.next()) {
						// the SP returns a single JSON_OBJECT AS result
						return rs.getString("result");
					}
				}
			}
			throw new IllegalStateException("No result returned from updatePaymentRequest");
		}
	}
    
}
