package com.monarchsolutions.sms.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monarchsolutions.sms.dto.paymentRequests.CreatePaymentRequestDTO;
import com.monarchsolutions.sms.dto.paymentRequests.PaymentRequestScheduleRule;
import com.monarchsolutions.sms.repository.PaymentRequestScheduleRepository;
import com.monarchsolutions.sms.repository.ScheduledJobLogRepository;

@Service
public class PaymentRequestSchedulerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentRequestSchedulerService.class);
    private static final String JOB_NAME = "payment_request_scheduler";

    private final PaymentRequestScheduleRepository scheduleRepository;
    private final PaymentRequestService paymentRequestService;
    private final ScheduledJobLogRepository jobLogRepository;
    private final ObjectMapper objectMapper;

    @Value("${jobs.payment-request-scheduler.enabled:true}")
    private boolean enabled;

    @Value("${jobs.payment-request-scheduler.lang:es}")
    private String defaultLang;

    public PaymentRequestSchedulerService(PaymentRequestScheduleRepository scheduleRepository,
                                          PaymentRequestService paymentRequestService,
                                          ScheduledJobLogRepository jobLogRepository,
                                          ObjectMapper objectMapper) {
        this.scheduleRepository = scheduleRepository;
        this.paymentRequestService = paymentRequestService;
        this.jobLogRepository = jobLogRepository;
        this.objectMapper = objectMapper;
    }

    public void execute(LocalDate referenceDate) {
        if (!enabled) {
            LOGGER.debug("{} job is disabled", JOB_NAME);
            return;
        }

        Instant startedAt = Instant.now();
        Long logId = jobLogRepository.insertStarted(JOB_NAME, LocalDateTime.now());

        LocalDate today = referenceDate != null ? referenceDate : LocalDate.now();
        List<PaymentRequestScheduleRule> rules;
        try {
            rules = scheduleRepository.findDueSchedules(today);
        } catch (Exception ex) {
            jobLogRepository.finalizeLog(
                logId,
                "FAILURE",
                0,
                0,
                0,
                startedAt,
                ex.getMessage(),
                stackTrace(ex),
                null
            );
            throw ex;
        }

        int processed = 0;
        int createdRequests = 0;
        int failedStudents = 0;
        String errorMessage = null;
        String errorStack = null;
        boolean hasFailures = false;

        List<Map<String, Object>> perRuleMetadata = new ArrayList<>();

        for (PaymentRequestScheduleRule rule : rules) {
            processed++;
            Map<String, Object> ruleMeta = new HashMap<>();
            ruleMeta.put("payment_request_scheduled_id", rule.getPaymentRequestScheduledId());
            ruleMeta.put("school_id", rule.getSchoolId());
            ruleMeta.put("group_id", rule.getGroupId());
            ruleMeta.put("student_id", rule.getStudentId());
            try {
                CreatePaymentRequestDTO payload = buildPayload(rule);
                Map<String, Object> response = paymentRequestService.createPaymentRequest(
                    rule.getCreatedBy(),
                    rule.getSchoolId(),
                    rule.getGroupId(),
                    rule.getStudentId(),
                    payload,
                    defaultLang
                );

                Map<String, Object> dataSection = extractDataSection(response);
                Object massUpload = dataSection.get("mass_upload");
                ruleMeta.put("mass_upload", massUpload);
                ruleMeta.put("response", response);

                createdRequests += extractCreatedCount(dataSection);
                failedStudents += extractFailedCount(dataSection);

                advanceSchedule(rule);

            } catch (Exception ex) {
                hasFailures = true;
                if (errorMessage == null) {
                    errorMessage = ex.getMessage();
                    errorStack = stackTrace(ex);
                }
                ruleMeta.put("error", ex.getMessage());
                LOGGER.error("Error executing schedule {}", rule.getPaymentRequestScheduledId(), ex);
            }
            perRuleMetadata.add(ruleMeta);
        }

        String metadataJson = null;
        try {
            Map<String, Object> metadata = Map.of(
                "reference_date", today.toString(),
                "rules", perRuleMetadata
            );
            metadataJson = objectMapper.writeValueAsString(metadata);
        } catch (Exception e) {
            LOGGER.warn("Unable to serialize job metadata", e);
        }

        String finalStatus;
        if (processed == 0 && !hasFailures) {
            finalStatus = "SUCCESS";
        } else if (!hasFailures) {
            finalStatus = "SUCCESS";
        } else if (processed == 0) {
            finalStatus = "FAILURE";
        } else {
            finalStatus = "PARTIAL_SUCCESS";
        }

        jobLogRepository.finalizeLog(
            logId,
            finalStatus,
            processed,
            createdRequests,
            failedStudents,
            startedAt,
            errorMessage,
            errorStack,
            metadataJson
        );
    }

    private String stackTrace(Exception ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    private void advanceSchedule(PaymentRequestScheduleRule rule) {
        LocalDate current = rule.getNextExecutionDate();
        LocalDate next = calculateNextExecutionDate(current, rule.getPeriodOfTimeId(), rule.getIntervalCount());
        if (next == null || (rule.getEndDate() != null && next.isAfter(rule.getEndDate()))) {
            scheduleRepository.deactivate(rule.getPaymentRequestScheduledId());
        } else {
            scheduleRepository.updateNextExecutionDate(rule.getPaymentRequestScheduledId(), next);
        }
        scheduleRepository.touchExecution(rule.getPaymentRequestScheduledId());
    }

    private LocalDate calculateNextExecutionDate(LocalDate current, Integer periodOfTimeId, Integer interval) {
        if (current == null || periodOfTimeId == null) {
            return null;
        }

        int steps = interval != null && interval > 0 ? interval : 1;
        ChronoUnit unit = switch (periodOfTimeId) {
            case 1 -> ChronoUnit.DAYS;
            case 2 -> ChronoUnit.WEEKS;
            case 3 -> ChronoUnit.MONTHS;
            case 4 -> ChronoUnit.YEARS;
            default -> ChronoUnit.MONTHS;
        };

        return current.plus(steps, unit);
    }

    private CreatePaymentRequestDTO buildPayload(PaymentRequestScheduleRule rule) {
        CreatePaymentRequestDTO dto = new CreatePaymentRequestDTO();
        dto.setPayment_concept_id(toInteger(rule.getPaymentConceptId()));
        dto.setAmount(rule.getAmount());
        dto.setPay_by(calculatePayBy(rule.getNextDueDate(), rule.getPaymentWindow()));
        dto.setComments(rule.getComments());
        dto.setLate_fee(rule.getLateFee());
        dto.setFee_type(rule.getFeeType());
        dto.setLate_fee_frequency(rule.getLateFeeFrequency() != null ? rule.getLateFeeFrequency().toString() : null);
        dto.setPayment_month(resolvePaymentMonth(rule));
        dto.setPartial_payment(rule.getPartialPayment() != null ? rule.getPartialPayment() : Boolean.FALSE);
        return dto;
    }

    private Integer toInteger(Long value) {
        return value != null ? value.intValue() : null;
    }

    private String resolvePaymentMonth(PaymentRequestScheduleRule rule) {
        Integer conceptId = toInteger(rule.getPaymentConceptId());
        if (conceptId == null || conceptId != 1) {
            return null;
        }

        if (rule.getPaymentMonth() != null && !rule.getPaymentMonth().isBlank()) {
            return normalizePaymentMonth(rule.getPaymentMonth());
        }

        LocalDate nextExecution = rule.getNextExecutionDate();
        if (nextExecution == null) {
            return null;
        }

        LocalDate firstDayOfMonth = nextExecution.withDayOfMonth(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return formatter.format(firstDayOfMonth);
    }

    private LocalDate calculatePayBy(LocalDate dueDate, Integer paymentWindow) {
        int windowDays = paymentWindow != null ? paymentWindow : 0;
        LocalDate baseDate = dueDate != null ? dueDate : LocalDate.now();
        return baseDate.plusDays(windowDays);
    }

    private String normalizePaymentMonth(String rawValue) {
        String trimmed = rawValue.trim();
        if (trimmed.matches("\\d{4}-\\d{2}$")) {
            return trimmed + "-01";
        }
        return trimmed;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> extractDataSection(Map<String, Object> response) {
        if (response == null) {
            return Map.of();
        }
        Object data = response.get("data");
        if (data instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return Map.of();
    }

    private int extractCreatedCount(Map<String, Object> dataSection) {
        Object count = dataSection.get("created_count");
        if (count instanceof Number number) {
            return number.intValue();
        }
        Object created = dataSection.get("created");
        if (created instanceof List<?> list) {
            return list.size();
        }
        return 0;
    }

    private int extractFailedCount(Map<String, Object> dataSection) {
        Object duplicatesCount = dataSection.get("duplicates_count");
        if (duplicatesCount instanceof Number number) {
            return number.intValue();
        }
        Object duplicates = dataSection.get("duplicates");
        if (duplicates instanceof List<?> list) {
            return list.size();
        }
        return 0;
    }
}
