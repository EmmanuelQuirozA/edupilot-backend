package com.monarchsolutions.sms.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.monarchsolutions.sms.dto.paymentRequests.PaymentRequestScheduleRule;

@Repository
public class PaymentRequestScheduleRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PaymentRequestScheduleRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<PaymentRequestScheduleRule> RULE_MAPPER = (rs, rowNum) -> {
        PaymentRequestScheduleRule rule = new PaymentRequestScheduleRule();
        rule.setPaymentRequestScheduledId(rs.getLong("payment_request_scheduled_id"));
        rule.setSchoolId(getLong(rs, "school_id"));
        rule.setGroupId(getLong(rs, "group_id"));
        rule.setStudentId(getLong(rs, "student_id"));
        rule.setPaymentConceptId(getLong(rs, "payment_concept_id"));
        rule.setAmount(rs.getBigDecimal("amount"));
        rule.setFeeType(rs.getString("fee_type"));
        rule.setLateFee(rs.getBigDecimal("late_fee"));
        rule.setLateFeeFrequency(getInteger(rs, "late_fee_frequency"));
        rule.setComments(rs.getString("comments"));
        rule.setNextExecutionDate(rs.getObject("next_execution_date", LocalDate.class));
        rule.setPaymentWindow(getInteger(rs, "payment_window"));
        rule.setPeriodOfTimeId(getInteger(rs, "period_of_time_id"));
        rule.setIntervalCount(getInteger(rs, "interval_count"));
        rule.setEndDate(rs.getObject("end_date", LocalDate.class));
        rule.setCreatedBy(getLong(rs, "created_by"));
        return rule;
    };

    private static Long getLong(ResultSet rs, String column) throws SQLException {
        long value = rs.getLong(column);
        return rs.wasNull() ? null : value;
    }

    private static Integer getInteger(ResultSet rs, String column) throws SQLException {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : value;
    }

    public List<PaymentRequestScheduleRule> findDueSchedules(LocalDate referenceDate) {
        String sql = """
            SELECT
                payment_request_scheduled_id,
                school_id,
                group_id,
                student_id,
                payment_concept_id,
                amount,
                fee_type,
                late_fee,
                late_fee_frequency,
                comments,
                next_execution_date,
                payment_window,
                period_of_time_id,
                interval_count,
                end_date,
                created_by
            FROM payment_request_scheduled
            WHERE active = 1
              AND next_execution_date IS NOT NULL
              AND next_execution_date <= :referenceDate
              AND (end_date IS NULL OR end_date >= :referenceDate)
        """;

        Map<String, Object> params = Map.of("referenceDate", referenceDate);
        return jdbcTemplate.query(sql, params, RULE_MAPPER);
    }

    public void updateNextExecutionDate(Long scheduleId, LocalDate nextExecutionDate) {
        String sql = """
            UPDATE payment_request_scheduled
               SET next_execution_date = :nextExecutionDate,
                   updated_at = NOW()
             WHERE payment_request_scheduled_id = :scheduleId
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("nextExecutionDate", nextExecutionDate)
            .addValue("scheduleId", scheduleId);

        jdbcTemplate.update(sql, params);
    }

    public void deactivate(Long scheduleId) {
        String sql = """
            UPDATE payment_request_scheduled
               SET active = 0,
                   updated_at = NOW()
             WHERE payment_request_scheduled_id = :scheduleId
        """;

        Map<String, Object> params = Map.of("scheduleId", scheduleId);
        jdbcTemplate.update(sql, params);
    }

    public void touchExecution(Long scheduleId) {
        String sql = """
            UPDATE payment_request_scheduled
               SET last_executed_at = NOW(),
                   updated_at = NOW()
             WHERE payment_request_scheduled_id = :scheduleId
        """;

        Map<String, Object> params = Map.of("scheduleId", scheduleId);
        jdbcTemplate.update(sql, params);
    }
}
