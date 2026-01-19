package com.monarchsolutions.sms.repository.etl;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentsLegacyEtlRepository {

    private final JdbcTemplate jdbcTemplate;

    public PaymentsLegacyEtlRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long findLegacySourceId(String sourceCode) {
        return jdbcTemplate.queryForObject(
                "SELECT legacy_source_id FROM legacy_sources WHERE source_code = ?",
                Long.class,
                sourceCode
        );
    }

    public long getOrCreateWatermark(long legacySourceId, String jobName) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT last_legacy_pk FROM etl_watermarks WHERE legacy_source_id = ? AND job_name = ?",
                    Long.class,
                    legacySourceId,
                    jobName
            );
        } catch (EmptyResultDataAccessException ex) {
            jdbcTemplate.update(
                    "INSERT INTO etl_watermarks (legacy_source_id, job_name, last_legacy_pk) VALUES (?, ?, ?)",
                    legacySourceId,
                    jobName,
                    0
            );
            return 0;
        }
    }

    public void updateWatermark(long legacySourceId, String jobName, long lastLegacyPk) {
        jdbcTemplate.update(
                "UPDATE etl_watermarks SET last_legacy_pk = ? WHERE legacy_source_id = ? AND job_name = ?",
                lastLegacyPk,
                legacySourceId,
                jobName
        );
    }

    public long createEtlRun(long legacySourceId, String jobName, long lastPkBefore, Instant startedAt) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO etl_runs (legacy_source_id, job_name, started_at, status, last_legacy_pk_before) " +
                            "VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            statement.setLong(1, legacySourceId);
            statement.setString(2, jobName);
            statement.setTimestamp(3, Timestamp.from(startedAt));
            statement.setString(4, "RUNNING");
            statement.setLong(5, lastPkBefore);
            return statement;
        }, keyHolder);
        Number key = keyHolder.getKey();
        return key == null ? 0L : key.longValue();
    }

    public void finishEtlRun(
            long etlRunId,
            String status,
            long lastPkAfter,
            long rowsRead,
            long rowsInserted,
            long rowsSkipped,
            long issuesCreated,
            long durationMs,
            Instant finishedAt
    ) {
        jdbcTemplate.update(
                "UPDATE etl_runs SET finished_at = ?, status = ?, last_legacy_pk_after = ?, rows_read = ?, " +
                        "rows_inserted = ?, rows_skipped = ?, issues_created = ?, duration_ms = ? WHERE etl_run_id = ?",
                Timestamp.from(finishedAt),
                status,
                lastPkAfter,
                rowsRead,
                rowsInserted,
                rowsSkipped,
                issuesCreated,
                durationMs,
                etlRunId
        );
    }

    public Optional<Long> findStudentId(long legacySourceId, long legacyStudentId) {
        return optionalLong(
                "SELECT student_id FROM student_legacy_map WHERE legacy_source_id = ? AND legacy_student_id = ?",
                legacySourceId,
                legacyStudentId
        );
    }

    public Optional<Long> findPaymentConceptId(long legacySourceId, String normalizedKey) {
        return optionalLong(
                "SELECT payment_concept_id FROM payment_concept_legacy_map " +
                        "WHERE legacy_source_id = ? AND legacy_concept_key_normalized = ?",
                legacySourceId,
                normalizedKey
        );
    }

    public Optional<Long> findPaymentThroughId(long legacySourceId, String normalizedKey) {
        return optionalLong(
                "SELECT payment_through_id FROM payment_through_legacy_map " +
                        "WHERE legacy_source_id = ? AND legacy_payment_through_key_normalized = ?",
                legacySourceId,
                normalizedKey
        );
    }

    public Optional<Long> findUserIdFromLegacyUserMap(long legacySourceId, long legacyUserId) {
        return optionalLong(
                "SELECT user_id FROM user_legacy_map WHERE legacy_source_id = ? AND legacy_user_id = ?",
                legacySourceId,
                legacyUserId
        );
    }

    public Optional<Long> findUserIdByEmail(String email) {
        return optionalLong("SELECT user_id FROM users WHERE email = ?", email);
    }

    public void insertUserLegacyMap(long legacySourceId, long legacyUserId, String legacyEmail, long userId) {
        jdbcTemplate.update(
                "INSERT INTO user_legacy_map (legacy_source_id, legacy_user_id, legacy_email, user_id) " +
                        "VALUES (?, ?, ?, ?)",
                legacySourceId,
                legacyUserId,
                legacyEmail,
                userId
        );
    }

    public void insertMappingIssue(
            long legacySourceId,
            String jobName,
            long legacyPk,
            String issueType,
            String issueDetails
    ) {
        jdbcTemplate.update(
                "INSERT INTO etl_mapping_issues (legacy_source_id, job_name, legacy_pk, issue_type, issue_details, created_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                legacySourceId,
                jobName,
                legacyPk,
                issueType,
                issueDetails,
                Timestamp.from(Instant.now())
        );
    }

    public int insertPayment(
            long studentId,
            long paymentConceptId,
            Integer paymentStatusId,
            Long validatedByUserId,
            LocalDate validatedAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String comments,
            Long paymentThroughId,
            String receiptFileName,
            LocalDate paymentDate,
            long legacySourceId,
            long legacyPaymentId,
            java.math.BigDecimal amount
    ) {
        return jdbcTemplate.update(
                "INSERT INTO payments (" +
                        "student_id, payment_concept_id, payment_month, amount, payment_status_id, " +
                        "validated_by_user_id, validated_at, created_at, updated_at, comments, " +
                        "payment_request_id, payment_through_id, receipt_path, receipt_file_name, payment_date, " +
                        "legacy_source_id, legacy_payment_id" +
                        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE updated_at = VALUES(updated_at)",
                studentId,
                paymentConceptId,
                null,
                amount,
                paymentStatusId,
                validatedByUserId,
                validatedAt == null ? null : java.sql.Date.valueOf(validatedAt),
                Timestamp.valueOf(createdAt),
                Timestamp.valueOf(updatedAt),
                comments,
                null,
                paymentThroughId,
                null,
                receiptFileName,
                paymentDate == null ? null : java.sql.Date.valueOf(paymentDate),
                legacySourceId,
                legacyPaymentId
        );
    }

    private Optional<Long> optionalLong(String sql, Object... args) {
        try {
            Long value = jdbcTemplate.queryForObject(sql, Long.class, args);
            return Optional.ofNullable(value);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }
}
