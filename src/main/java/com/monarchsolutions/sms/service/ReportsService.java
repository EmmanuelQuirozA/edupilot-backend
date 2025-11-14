package com.monarchsolutions.sms.service;
import java.util.concurrent.atomic.AtomicReference;

import com.monarchsolutions.sms.dto.common.PageResult;
import com.monarchsolutions.sms.dto.reports.PaymentRequestDetailsResponseV2;
import com.monarchsolutions.sms.dto.reports.PaymentRequestDetailsResponseV2.BreakdownEntry;
import com.monarchsolutions.sms.dto.reports.PaymentRequestDetailsResponseV2.PaymentDetail;
import com.monarchsolutions.sms.dto.reports.PaymentRequestDetailsResponseV2.PaymentInfoSummary;
import com.monarchsolutions.sms.repository.ReportsRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportsService {

    @Autowired
    private ReportsRepository reportsRepository;

    @Transactional(readOnly = true)
    public PageResult<Map<String,Object>> getPaymentsPivotReport(
        Long school_id,
        Long student_id,
        Long payment_id,
        Long payment_request_id,
        LocalDate start_date,
        LocalDate end_date,
        Boolean group_status,
        Boolean user_status,
        String student_full_name,
        String payment_reference,
        String generation,
        String grade_group,
        String scholar_level,
        String lang,
        int offset,
        int limit,
        Boolean exportAll,
        Boolean showDebtOnly,
        String order_by,
        String order_dir
    ) throws Exception {
        return reportsRepository.getPaymentsPivotReport(
            school_id,
            student_id,
            start_date,
            end_date,
            group_status,
            user_status,
            student_full_name,
            payment_reference,
            generation,
            grade_group,
            scholar_level,
            lang,
            offset,
            limit,
            exportAll,
            showDebtOnly,
            order_by,
            order_dir
        );
    }

    @Transactional(readOnly = true)
    public PageResult<Map<String,Object>> getPayments(
        Long token_user_id,
        Long school_id,
        Long student_id,
        Long payment_id,
        Long payment_request_id,
        String student_full_name,
        String payment_reference,
        String generation,
        String grade_group,
        String pt_name,
        String scholar_level_name,
        LocalDate payment_month,
        Date payment_created_at,
        String lang,
        int page,
        int size,
        Boolean exportAll,
        String order_by,
        String order_dir
    ) throws Exception {
        return reportsRepository.getPayments(
            token_user_id,
            school_id,
            student_id,
            payment_id,
            payment_request_id,
            student_full_name,
            payment_reference,
            generation,
            grade_group,
            pt_name,
            scholar_level_name,
            payment_month,
            payment_created_at,
            lang,
            page,
            size,
            exportAll,
            order_by,
            order_dir
        );
    }


    @Transactional(readOnly = true)
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
        int offset,
        int limit,
        Boolean exportAll,
        String orderBy,
        String orderDir
    ) throws Exception {
        boolean exportAllFlag = exportAll != null && exportAll;
        return reportsRepository.getPaymentRecurrences(
            tokenUserId,
            schoolId,
            studentId,
            paymentRequestId,
            paymentMonth,
            ptName,
            paymentReference,
            studentFullName,
            scEnabled,
            uEnabled,
            gEnabled,
            gradeGroup,
            lang,
            orderBy,
            orderDir,
            offset,
            limit,
            exportAllFlag
        );
    }



    @Transactional(readOnly = true)
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
    ) throws Exception {
        return reportsRepository.getPaymentRequests(
			token_user_id,
			school_id,
			student_id,
			payment_request_id,
			pr_created_start,
			pr_created_end,
			pr_pay_by_start,
			pr_pay_by_end,
			payment_month,
			ps_pr_name,
			pt_name,
			payment_reference,
			student_full_name,
			sc_enabled,
			u_enabled,
			g_enabled,
			pr_payment_status_id,
			grade_group,
			lang,
			order_by,
			order_dir,
			page,
			size,
			export_all
        );
    }

    @Transactional(readOnly = true)
    public PageResult<Map<String,Object>> getBalanceRecharges(
        Long token_school_id,
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
    ) throws Exception {
        return reportsRepository.getBalanceRecharges(
        token_school_id,
        user_id,
        school_id,
        full_name,
        created_at,
        lang,
        page,
        size,
        exportAll,
        order_by,
        order_dir
        );
    }
        
    public String updatePaymentRequest(long paymentRequestId, long responsableUserId, Map<String,Object> jsonData, String lang) throws Exception {
        return reportsRepository.updatePaymentRequest(paymentRequestId, responsableUserId, jsonData, lang);
    }

    public PaymentRequestDetailsResponseV2.PaymentRequestDetailsResponse getPaymentRequestDetails(
            Long tokenUserId,
            Long schoolId,
            Long paymentRequestId,
            String lang
    ) throws SQLException {
        // 1) call SP and map into V2 DTO
        var resp = reportsRepository.getPaymentRequestDetails(tokenUserId, schoolId, paymentRequestId, lang);

        // fee_type and pr_amount come from resp.getPaymentRequest()
        var pr = resp.getPaymentRequest();
        BigDecimal prAmount      = pr.getPr_amount();
        BigDecimal lateFee       = pr.getLate_fee();
        int      freq            = pr.getLate_fee_frequency();
        LocalDateTime closedAt   = pr.getClosed_at();
        
        LocalDate payBy;
        if (pr.getPr_pay_by() != null) {
            payBy = pr.getPr_pay_by().toLocalDate();
        } else {
            // fallback: use creation date or today
            payBy = pr.getPr_created_at() != null
                ? pr.getPr_created_at().toLocalDate()
                : LocalDate.now();
        }
        String   feeType         = pr.getFee_type();

        // determine how many days late:
        List<BreakdownEntry> fees = new ArrayList<>();
        fees.add(new BreakdownEntry(
            null,
            "initial_payment_request",
            null,
            pr.getPs_pr_name(),
            pr.getPr_pay_by(),
            prAmount.negate(),
            null
        ));

        // 2) build fee entries until balance ≥ 0
        AtomicReference<BigDecimal> running = new AtomicReference<>(prAmount.negate());
        LocalDate cursor = pr.getPr_pay_by().toLocalDate().plusDays(freq);
        
        LocalDate lastPaymentDate = resp.getPayments().stream()
        .map(p -> {
            if (p.getPay_created_at() != null) {
                return p.getPay_created_at().toLocalDate();
            } else {
                return LocalDate.MIN;
            }
        })
        .max(LocalDate::compareTo)
        .orElse(LocalDate.now());

        // we’ll keep generating fees day by day until our running balance + payments ≥ 0
        while (true) {
        // 1) Stop if we’ve gone past closedAt:
        if (closedAt != null
            && cursor.atStartOfDay().isAfter(closedAt.toLocalDate().atStartOfDay())) {
            break;
        }
        // 2) stop if we’ve gone past both now and the last payment date
        if (cursor.isAfter(LocalDate.now()) && cursor.isAfter(lastPaymentDate)) {
            break;
        }

        BigDecimal feeAmt = "$".equals(feeType)
            ? lateFee.negate()
            : prAmount.multiply(lateFee)
                .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP)
                .negate();

        fees.add(new BreakdownEntry(
            null,
            "late_fee",
            null,
            null,
            cursor.atStartOfDay(),
            feeAmt,
            null
        ));
        running.set(running.get().add(feeAmt)); // Update the running balance

        // snapshot the current cursor so the lambda can use it
        final LocalDate feeDate = cursor;

        BigDecimal paidUpToFeeDate = resp.getPayments().stream()
            .filter(p -> !p.getPay_created_at().toLocalDate().isAfter(feeDate))
            .map(PaymentDetail::getAmount)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // stop if after adding payments up to this day we’re back to non-negative
        if (running.get().add(paidUpToFeeDate).compareTo(BigDecimal.ZERO) >= 0) {
            break;
        }

        cursor = cursor.plusDays(freq);
        }

        // 3) payments entries:
        List<BreakdownEntry> payments = resp.getPayments().stream()
            .map(p -> {
                BigDecimal paymentAmount = p.getAmount();
                Long paymentStatusId = p.getPayment_status_id();

                // Only update balance for non-rejected payments
                if (paymentStatusId != 4) { // If the payment is not rejected
                    running.set(running.get().add(paymentAmount));  // Update balance for non-rejected payments
                }

                // Add payment entry to the breakdown (including rejected ones)
                return new BreakdownEntry(
                    p.getPayment_id(),
                    "payment",
                    p.getPayment_status_id(),
                    p.getPayment_status_name(),
                    p.getPay_created_at(),
                    paymentAmount,
                    paymentStatusId == 4 ? running.get() : null // Keep balance null for rejected payments
                );
            })
            .collect(Collectors.toList());

        // 4) merge, sort and recompute final balances:
        List<BreakdownEntry> combined = new ArrayList<>(fees);
        
        combined.addAll(payments);
        combined.sort(Comparator.comparing(BreakdownEntry::getDate));

        running.set(BigDecimal.ZERO); // Reset running balance to 0
        for (BreakdownEntry e : combined) {
            // Skip updating the balance for rejected payments (those with null balance)
            if (e.getPayment_status_id() == null || e.getPayment_status_id() != 4) {
                running.set(running.get().add(e.getAmount()));  // Only update balance for non-rejected payments
            }
            e.setBalance(running.get());  // Set the balance for each entry
        }

        resp.setBreakdown(combined);
        
        // 5) closed_at entry at the very end
        if (closedAt != null) {
            combined.add(new BreakdownEntry(
                null,                    // no payment_id
                "closed_at",             // type
                null,                    // no status
                pr.getPs_pr_name(),                // you can localize or pull a name from pr
                closedAt,                // timestamp
                null,         // amount = 0
                running.get()            // final balance
            ));
            // re-sort so it really goes last (or just trust it’s the newest date)
            combined.sort(Comparator.comparing(BreakdownEntry::getDate));
        }

        // 6) now build summary from exactly those two lists:
        // 1) sum only the actual late‐fee entries, not the initial_payment_request:
        BigDecimal accumulatedFees = fees.stream()
        .filter(e -> "late_fee".equals(e.getType()))   // ignore the "initial_payment_request"
        .map(BreakdownEntry::getAmount)                 // e.g. –10, –10, …
        .reduce(BigDecimal.ZERO, BigDecimal::add)       // –50
        .negate();              
        // 2) other values stay the same:
        BigDecimal latePeriods     = BigDecimal.valueOf(
            fees.stream().filter(e -> "late_fee".equals(e.getType())).count()
        );                        // +50
        BigDecimal totalPaid       = resp.getPayments().stream()
            .filter(p -> p.getPayment_status_id() != 4)
            .map(PaymentDetail::getAmount)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);          // e.g. 1230
        BigDecimal pendingPayment  = prAmount.subtract(totalPaid).add(accumulatedFees);

        PaymentInfoSummary summary = new PaymentInfoSummary(
            totalPaid,                     // 1230.00
            latePeriods.longValue(),       // 5
            prAmount,                      // still the request total if you want
            accumulatedFees,               //  50.00
            pendingPayment                 //  20.00
        );
        resp.setPaymentInfo(summary);

        return resp;
    }
}