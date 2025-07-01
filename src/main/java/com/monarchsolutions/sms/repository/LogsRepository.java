package com.monarchsolutions.sms.repository;

import java.text.SimpleDateFormat;
import java.time.LocalDate;

import com.monarchsolutions.sms.dto.common.PageResult;
import com.monarchsolutions.sms.dto.userLogs.UserLogsListDto;
import com.monarchsolutions.sms.dto.userLogs.paymentRequest.PaymentRequestLogsDto;
import com.monarchsolutions.sms.dto.userLogs.payments.PaymentLogsDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

import javax.sql.DataSource;

@Repository
public class LogsRepository {
	
	@Autowired
	private DataSource dataSource;

	@PersistenceContext
	private EntityManager entityManager;

	// Get Users Activity Logs List
	public List<UserLogsListDto> getUsersActivityLog(Long tokenSchoolId, String lang){
		// Create the stored procedure query
		StoredProcedureQuery query = entityManager.createStoredProcedureQuery("getUsersActivityLog");

				// Register IN parameters
		query.registerStoredProcedureParameter("user_school_id", Long.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);

		// Set the parameter values
		query.setParameter("user_school_id", tokenSchoolId);
		query.setParameter("lang", lang);

		// Execute the stored procedure
		query.execute();

		// Retrieve the results as a list of Object arrays
		@SuppressWarnings("unchecked")
		List<Object[]> results = query.getResultList();
		List<UserLogsListDto> userLogs = new ArrayList<>();

		for (Object[] data : results) {
			userLogs.add(mapStudent(data));
		}
		return userLogs;
	}

	private UserLogsListDto mapStudent(Object[] data) {
		UserLogsListDto student = new UserLogsListDto();
		student.setActivity_log_users_id(data[0] != null ? ((Number) data[0]).longValue() : null);
		student.setResponsible_fullname(data[1] != null ? ((String) data[1]) : null);
		student.setTarget_fullname(data[2] != null ? (String) data[2] : null);
		student.setSchool_commercial_name(data[3] != null ? (String) data[3] : null);
		student.setUsername(data[4] != null ? ((String) data[4]) : null);
		student.setFull_name(data[5] != null ? ((String) data[5]) : null);
		// birth_date_formated (index 40): sometimes returned as a Date, so format it.
		student.setCreated_at(
			data[6] != null 
			? (data[6] instanceof java.sql.Date 
					? new SimpleDateFormat("MM-dd-yyyy").format((java.sql.Date) data[6])
					: data[6].toString())
			: null
		);
		student.setRole_name(data[7] != null ? ((String) data[7]) : null);
		student.setCategory(data[8] != null ? (String) data[8] : null);
		student.setLog_description(data[9] != null ? (String) data[9] : null);
		student.setLog_action(data[10] != null ? (String) data[10] : null);
		
		return student;
	}

	
	// Get payment request Activity Logs List
	public List<PaymentRequestLogsDto> getPaymentRequestLogs(Long token_user_id,Long tokenSchoolId, Long payment_request_id, String lang){
		// Create the stored procedure query
		StoredProcedureQuery query = entityManager.createStoredProcedureQuery("getPaymentRequestLogs");

		// Register IN parameters
		query.registerStoredProcedureParameter("token_user_id", Long.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("payment_request_id", Long.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("user_school_id", Long.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);

		// Set the parameter values
		query.setParameter("token_user_id", token_user_id);
		query.setParameter("payment_request_id", payment_request_id);
		query.setParameter("user_school_id", tokenSchoolId);
		query.setParameter("lang", lang);

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
		List<PaymentRequestLogsDto> logs = new ArrayList<>(rows.size());
		for (Object[] data : rows) {
			logs.add(mapPaymentRequestLogs(data));
		}
		return logs;
	}

	private PaymentRequestLogsDto mapPaymentRequestLogs(Object[] data) {
		PaymentRequestLogsDto dto = new PaymentRequestLogsDto();
		dto.setPayment_request_id(data[0] != null ? ((Number) data[0]).longValue() : null);
		dto.setResponsable_user_id(data[1] != null ? ((Number) data[1]).longValue() : null);
		dto.setResponsable_full_name(data[2] != null ? (String) data[2] : null);
		dto.setRole_name(data[3] != null ? (String) data[3] : null);
		dto.setLog_type_name(data[4] != null ? ((String) data[4]) : null);
		dto.setField(data[5] != null ? ((String) data[5]) : null);
		dto.setFrom(data[6] != null ? ((String) data[6]) : null);
		dto.setTo(data[7] != null ? ((String) data[7]) : null);
		dto.setComments(data[8] != null ? ((String) data[8]) : null);
		Object raw = data[9];
    if (raw instanceof java.sql.Timestamp) {
        dto.setUpdated_at(((java.sql.Timestamp) raw).toLocalDateTime());
    } else {
        dto.setUpdated_at(null);
    }
		
		return dto;
	}
	

	// --------------------------------
	// Get payment request Activity Logs List
	public List<PaymentLogsDto> getPaymentLogs(Long token_user_id,Long tokenSchoolId, Long payment_request_id, String lang){
		// Create the stored procedure query
		StoredProcedureQuery query = entityManager.createStoredProcedureQuery("getPaymentLogs");

		// Register IN parameters
		query.registerStoredProcedureParameter("token_user_id", Long.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("payment_request_id", Long.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("user_school_id", Long.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);

		// Set the parameter values
		query.setParameter("token_user_id", token_user_id);
		query.setParameter("payment_request_id", payment_request_id);
		query.setParameter("user_school_id", tokenSchoolId);
		query.setParameter("lang", lang);

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
		List<PaymentLogsDto> logs = new ArrayList<>(rows.size());
		for (Object[] data : rows) {
			logs.add(mapPaymentLogs(data));
		}
		return logs;
	}

	private PaymentLogsDto mapPaymentLogs(Object[] data) {
		PaymentLogsDto dto = new PaymentLogsDto();
		dto.setPayment_id(data[0] != null ? ((Number) data[0]).longValue() : null);
		dto.setResponsable_user_id(data[1] != null ? ((Number) data[1]).longValue() : null);
		dto.setResponsable_full_name(data[2] != null ? (String) data[2] : null);
		dto.setRole_name(data[3] != null ? (String) data[3] : null);
		dto.setLog_type_name(data[4] != null ? ((String) data[4]) : null);
		dto.setField(data[5] != null ? ((String) data[5]) : null);
		dto.setFrom(data[6] != null ? ((String) data[6]) : null);
		dto.setTo(data[7] != null ? ((String) data[7]) : null);
		dto.setComments(data[8] != null ? ((String) data[8]) : null);
		Object raw = data[9];
    if (raw instanceof java.sql.Timestamp) {
        dto.setUpdated_at(((java.sql.Timestamp) raw).toLocalDateTime());
    } else {
        dto.setUpdated_at(null);
    }
		
		return dto;
	}

}
