package com.monarchsolutions.sms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.monarchsolutions.sms.util.JwtUtil;
import com.monarchsolutions.sms.dto.userLogs.UserLogsListDto;
import com.monarchsolutions.sms.dto.userLogs.paymentRequest.PaymentRequestLogGroupDto;
import com.monarchsolutions.sms.dto.userLogs.paymentRequest.PaymentRequestLogsDto;
import com.monarchsolutions.sms.dto.userLogs.payments.PaymentLogGroupDto;
import com.monarchsolutions.sms.dto.userLogs.payments.PaymentLogsDto;
import com.monarchsolutions.sms.service.LogsService;

@RestController
@RequestMapping("/api/logs")
public class LogsController {
    
    @Autowired
    private LogsService UserLogsService;

    @Autowired
    private JwtUtil jwtUtil;
    
    // Endpoint for retrieving the list of usersLogs.
    @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
    @GetMapping("/list")
    public ResponseEntity<?> getUsersActivityLog(
                                        // @RequestHeader("Authorization") String authHeader,
                                        @RequestParam(required = false) Long school_id,
                                        @RequestParam(defaultValue = "es") String lang) {
        try {
            // String token = authHeader.substring(7);
            // Long tokenSchoolId = jwtUtil.extractSchoolId(token);
            List<UserLogsListDto> usersLogs = UserLogsService.getUsersActivityLog(school_id, lang);
            return ResponseEntity.ok(usersLogs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint for retrieving the list of paymentRequestLogs.
    @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
    @GetMapping("/payment-requests/{paymentRequestId}")
    public ResponseEntity<List<PaymentRequestLogGroupDto>> getPaymentRequestLogs(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("paymentRequestId") Long paymentRequestId,
            @RequestParam(defaultValue = "es") String lang
    ) {
        // strip "Bearer "
        String token = authHeader.substring(7);
        Long schoolId = jwtUtil.extractSchoolId(token);
        Long token_user_id = jwtUtil.extractUserId(token);

        List<PaymentRequestLogGroupDto> grouped = UserLogsService.getGroupedRequestLogs(
            token_user_id,    
            schoolId,
            paymentRequestId,
            lang
        );
        return ResponseEntity.ok(grouped);
    }
    
    // Endpoint for retrieving the list of paymentLogs.
    @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<List<PaymentLogGroupDto>> getPaymentLogs(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("paymentId") Long paymentId,
            @RequestParam(defaultValue = "es") String lang
    ) {
        // strip "Bearer "
        String token = authHeader.substring(7);
        Long schoolId = jwtUtil.extractSchoolId(token);
        Long token_user_id = jwtUtil.extractUserId(token);

        List<PaymentLogGroupDto> grouped = UserLogsService.getGroupedPaymentLogs(
            token_user_id,    
            schoolId,
            paymentId,
            lang
        );
        return ResponseEntity.ok(grouped);
    }

}
