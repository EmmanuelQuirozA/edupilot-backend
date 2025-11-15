package com.monarchsolutions.sms.repository;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ScheduledJobLogRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ScheduledJobLogRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long insertStarted(String jobName, LocalDateTime executionDate) {
        String sql = """
            INSERT INTO scheduled_job_log (
                job_name,
                execution_date,
                status,
                rules_processed,
                requests_created,
                failed_students,
                duration_ms,
                error_message,
                error_stack,
                metadata
            ) VALUES (
                :jobName,
                :executionDate,
                'STARTED',
                0,
                0,
                0,
                NULL,
                NULL,
                NULL,
                NULL
            )
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("jobName", jobName)
            .addValue("executionDate", executionDate);

        jdbcTemplate.update(sql, params);
        return jdbcTemplate.getJdbcTemplate().queryForObject("SELECT LAST_INSERT_ID()", Long.class);
    }

    public void finalizeLog(Long logId,
                            String status,
                            int rulesProcessed,
                            int requestsCreated,
                            int failedStudents,
                            Instant startedAt,
                            String errorMessage,
                            String errorStack,
                            String metadataJson) {
        long durationMs = Duration.between(startedAt, Instant.now()).toMillis();

        String sql = """
            UPDATE scheduled_job_log
               SET status = :status,
                   rules_processed = :rulesProcessed,
                   requests_created = :requestsCreated,
                   failed_students = :failedStudents,
                   duration_ms = :duration,
                   error_message = :errorMessage,
                   error_stack = :errorStack,
                   metadata = :metadata,
                   created_at = created_at
             WHERE scheduled_job_log_id = :logId
        """;

        Map<String, Object> params = new HashMap<>();
        params.put("status", status);
        params.put("rulesProcessed", rulesProcessed);
        params.put("requestsCreated", requestsCreated);
        params.put("failedStudents", failedStudents);
        params.put("duration", durationMs);
        params.put("errorMessage", errorMessage);
        params.put("errorStack", errorStack);
        params.put("metadata", metadataJson);
        params.put("logId", logId);

        jdbcTemplate.update(sql, params);
    }
}
