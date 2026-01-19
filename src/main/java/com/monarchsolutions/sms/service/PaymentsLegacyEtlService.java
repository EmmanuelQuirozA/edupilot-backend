package com.monarchsolutions.sms.service;

import com.monarchsolutions.sms.etl.LegacyPagoRecord;
import com.monarchsolutions.sms.etl.LegacyPagoRowMapper;
import com.monarchsolutions.sms.etl.PaymentsLegacyEtlResult;
import com.monarchsolutions.sms.repository.etl.PaymentsLegacyEtlRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class PaymentsLegacyEtlService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentsLegacyEtlService.class);
    private static final String JOB_NAME = "payments";
    private static final int DEFAULT_PAYMENT_STATUS_ID = 1;
    private static final LegacyPagoRowMapper LEGACY_PAGO_ROW_MAPPER = new LegacyPagoRowMapper();
    private static final String LEGACY_PAGOS_QUERY = "SELECT idPago, idAlumno, concepto, monto, fechaRegistro, " +
            "fechaPago, formaPago, estatusPago, comprobante, observaciones, idUsuarioAprobo, fechaAprobacion " +
            "FROM pagos WHERE idPago > ? ORDER BY idPago LIMIT ?";

    private final JdbcTemplate legacyJdbcTemplate;
    private final PaymentsLegacyEtlRepository repository;
    private final TransactionTemplate transactionTemplate;

    public PaymentsLegacyEtlService(
            @Qualifier("legacyJdbcTemplate") JdbcTemplate legacyJdbcTemplate,
            PaymentsLegacyEtlRepository repository,
            PlatformTransactionManager transactionManager
    ) {
        this.legacyJdbcTemplate = legacyJdbcTemplate;
        this.repository = repository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public PaymentsLegacyEtlResult run(String sourceCode, int batchSize) {
        Instant startedAt = Instant.now();
        long legacySourceId = repository.findLegacySourceId(sourceCode);
        long lastPkBefore = repository.getOrCreateWatermark(legacySourceId, JOB_NAME);
        long etlRunId = repository.createEtlRun(legacySourceId, JOB_NAME, lastPkBefore, startedAt);

        long currentPk = lastPkBefore;
        long totalRead = 0;
        long totalInserted = 0;
        long totalSkipped = 0;
        long totalIssues = 0;
        String status = "SUCCESS";

        try {
            while (true) {
                List<LegacyPagoRecord> records = legacyJdbcTemplate.query(
                        LEGACY_PAGOS_QUERY,
                        LEGACY_PAGO_ROW_MAPPER,
                        currentPk,
                        batchSize
                );
                if (records.isEmpty()) {
                    break;
                }
                totalRead += records.size();
                long batchMaxPk = records.get(records.size() - 1).getIdPago();

                BatchOutcome outcome = transactionTemplate.execute(statusTransaction ->
                        processBatch(legacySourceId, records)
                );
                if (outcome == null) {
                    throw new IllegalStateException("Batch transaction returned null outcome.");
                }

                totalInserted += outcome.rowsInserted;
                totalSkipped += outcome.rowsSkipped;
                totalIssues += outcome.issuesCreated;
                repository.updateWatermark(legacySourceId, JOB_NAME, batchMaxPk);
                currentPk = batchMaxPk;

                logger.info(
                        "Payments legacy ETL batch completed sourceCode={} legacySourceId={} rowsRead={} " +
                                "rowsInserted={} rowsSkipped={} issuesCreated={} lastLegacyPk={}",
                        sourceCode,
                        legacySourceId,
                        records.size(),
                        outcome.rowsInserted,
                        outcome.rowsSkipped,
                        outcome.issuesCreated,
                        batchMaxPk
                );

                if (records.size() < batchSize) {
                    break;
                }
            }
        } catch (Exception ex) {
            status = "FAILED";
            logger.error("Payments legacy ETL failed sourceCode={}", sourceCode, ex);
            throw ex;
        } finally {
            long durationMs = java.time.Duration.between(startedAt, Instant.now()).toMillis();
            repository.finishEtlRun(
                    etlRunId,
                    status,
                    currentPk,
                    totalRead,
                    totalInserted,
                    totalSkipped,
                    totalIssues,
                    durationMs,
                    Instant.now()
            );
        }

        long durationMs = java.time.Duration.between(startedAt, Instant.now()).toMillis();
        return new PaymentsLegacyEtlResult(
                lastPkBefore,
                currentPk,
                totalRead,
                totalInserted,
                totalSkipped,
                totalIssues,
                durationMs
        );
    }

    private BatchOutcome processBatch(long legacySourceId, List<LegacyPagoRecord> records) {
        long rowsInserted = 0;
        long rowsSkipped = 0;
        long issuesCreated = 0;

        for (LegacyPagoRecord record : records) {
            try {
                Optional<Long> studentId = resolveStudentId(legacySourceId, record);
                if (studentId.isEmpty()) {
                    rowsSkipped++;
                    issuesCreated++;
                    continue;
                }

                Optional<Long> paymentConceptId = resolvePaymentConceptId(legacySourceId, record);
                if (paymentConceptId.isEmpty()) {
                    rowsSkipped++;
                    issuesCreated++;
                    continue;
                }

                PaymentThroughResolution paymentThroughResolution = resolvePaymentThroughId(legacySourceId, record);
                issuesCreated += paymentThroughResolution.issuesCreated;

                UserResolution userResolution = resolveValidatedByUserId(legacySourceId, record);
                issuesCreated += userResolution.issuesCreated;

                LocalDateTime createdAt = record.getFechaRegistro() == null
                        ? LocalDateTime.now()
                        : LocalDateTime.of(record.getFechaRegistro(), LocalTime.MIDNIGHT);
                LocalDateTime updatedAt = LocalDateTime.now();

                int updateCount = repository.insertPayment(
                        studentId.get(),
                        paymentConceptId.get(),
                        DEFAULT_PAYMENT_STATUS_ID,
                        userResolution.userId,
                        record.getFechaAprobacion(),
                        createdAt,
                        updatedAt,
                        record.getObservaciones(),
                        paymentThroughResolution.paymentThroughId,
                        record.getComprobante(),
                        record.getFechaPago(),
                        legacySourceId,
                        record.getIdPago(),
                        record.getMonto()
                );

                if (updateCount == 1) {
                    rowsInserted++;
                } else {
                    rowsSkipped++;
                }
            } catch (Exception ex) {
                rowsSkipped++;
                issuesCreated++;
                repository.insertMappingIssue(
                        legacySourceId,
                        JOB_NAME,
                        record.getIdPago(),
                        "PROCESSING_ERROR",
                        "Unexpected error: " + ex.getMessage()
                );
                logger.warn("Payments legacy ETL record failed idPago={}", record.getIdPago(), ex);
            }
        }

        return new BatchOutcome(rowsInserted, rowsSkipped, issuesCreated);
    }

    private Optional<Long> resolveStudentId(long legacySourceId, LegacyPagoRecord record) {
        if (record.getIdAlumno() == null) {
            repository.insertMappingIssue(
                    legacySourceId,
                    JOB_NAME,
                    record.getIdPago(),
                    "MISSING_LEGACY_STUDENT_ID",
                    "Legacy record missing idAlumno."
            );
            return Optional.empty();
        }
        Optional<Long> studentId = repository.findStudentId(legacySourceId, record.getIdAlumno());
        if (studentId.isEmpty()) {
            repository.insertMappingIssue(
                    legacySourceId,
                    JOB_NAME,
                    record.getIdPago(),
                    "STUDENT_MAPPING_NOT_FOUND",
                    "No student_legacy_map match for idAlumno=" + record.getIdAlumno()
            );
        }
        return studentId;
    }

    private Optional<Long> resolvePaymentConceptId(long legacySourceId, LegacyPagoRecord record) {
        String normalized = normalizeKey(record.getConcepto());
        if (normalized == null) {
            repository.insertMappingIssue(
                    legacySourceId,
                    JOB_NAME,
                    record.getIdPago(),
                    "MISSING_CONCEPTO",
                    "Legacy record missing concepto."
            );
            return Optional.empty();
        }
        Optional<Long> conceptId = repository.findPaymentConceptId(legacySourceId, normalized);
        if (conceptId.isEmpty()) {
            repository.insertMappingIssue(
                    legacySourceId,
                    JOB_NAME,
                    record.getIdPago(),
                    "CONCEPT_MAPPING_NOT_FOUND",
                    "No payment_concept_legacy_map match for concepto=" + normalized
            );
        }
        return conceptId;
    }

    private PaymentThroughResolution resolvePaymentThroughId(long legacySourceId, LegacyPagoRecord record) {
        String normalized = normalizeKey(record.getFormaPago());
        if (normalized == null) {
            return new PaymentThroughResolution(null, 0);
        }
        Optional<Long> paymentThroughId = repository.findPaymentThroughId(legacySourceId, normalized);
        if (paymentThroughId.isEmpty()) {
            repository.insertMappingIssue(
                    legacySourceId,
                    JOB_NAME,
                    record.getIdPago(),
                    "PAYMENT_THROUGH_MAPPING_NOT_FOUND",
                    "No payment_through_legacy_map match for formaPago=" + normalized +
                            ". Proceeding with null payment_through_id."
            );
            return new PaymentThroughResolution(null, 1);
        }
        return new PaymentThroughResolution(paymentThroughId.get(), 0);
    }

    private UserResolution resolveValidatedByUserId(long legacySourceId, LegacyPagoRecord record) {
        if (record.getIdUsuarioAprobo() == null) {
            return new UserResolution(null, 0);
        }
        Optional<Long> mappedUser = repository.findUserIdFromLegacyUserMap(
                legacySourceId,
                record.getIdUsuarioAprobo()
        );
        if (mappedUser.isPresent()) {
            return new UserResolution(mappedUser.get(), 0);
        }

        Optional<String> legacyEmail = findLegacyUserEmail(record.getIdUsuarioAprobo());
        if (legacyEmail.isEmpty()) {
            repository.insertMappingIssue(
                    legacySourceId,
                    JOB_NAME,
                    record.getIdPago(),
                    "LEGACY_USER_EMAIL_NOT_FOUND",
                    "No legacy email found for idUsuarioAprobo=" + record.getIdUsuarioAprobo()
            );
            return new UserResolution(null, 1);
        }

        Optional<Long> userId = repository.findUserIdByEmail(legacyEmail.get());
        if (userId.isPresent()) {
            repository.insertUserLegacyMap(
                    legacySourceId,
                    record.getIdUsuarioAprobo(),
                    legacyEmail.get(),
                    userId.get()
            );
            return new UserResolution(userId.get(), 0);
        }

        repository.insertMappingIssue(
                legacySourceId,
                JOB_NAME,
                record.getIdPago(),
                "VALIDATED_USER_NOT_FOUND",
                "No users match for legacy approver email=" + legacyEmail.get()
        );
        return new UserResolution(null, 1);
    }

    private Optional<String> findLegacyUserEmail(Long legacyUserId) {
        try {
            // TODO: Adjust this query to the legacy users/person table as needed for your schema.
            String email = legacyJdbcTemplate.queryForObject(
                    "SELECT email FROM usuarios WHERE idUsuario = ?",
                    String.class,
                    legacyUserId
            );
            return Optional.ofNullable(email);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    private String normalizeKey(String input) {
        if (input == null) {
            return null;
        }
        String trimmed = input.trim().toLowerCase();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed.replaceAll("\\s+", " ");
    }

    private static class BatchOutcome {
        private final long rowsInserted;
        private final long rowsSkipped;
        private final long issuesCreated;

        private BatchOutcome(long rowsInserted, long rowsSkipped, long issuesCreated) {
            this.rowsInserted = rowsInserted;
            this.rowsSkipped = rowsSkipped;
            this.issuesCreated = issuesCreated;
        }
    }

    private static class PaymentThroughResolution {
        private final Long paymentThroughId;
        private final long issuesCreated;

        private PaymentThroughResolution(Long paymentThroughId, long issuesCreated) {
            this.paymentThroughId = paymentThroughId;
            this.issuesCreated = issuesCreated;
        }
    }

    private static class UserResolution {
        private final Long userId;
        private final long issuesCreated;

        private UserResolution(Long userId, long issuesCreated) {
            this.userId = userId;
            this.issuesCreated = issuesCreated;
        }
    }
}
