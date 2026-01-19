package com.monarchsolutions.sms.controller;

import com.monarchsolutions.sms.etl.PaymentsLegacyEtlResult;
import com.monarchsolutions.sms.service.PaymentsLegacyEtlService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/etl/payments")
public class PaymentsLegacyEtlController {

    private final PaymentsLegacyEtlService paymentsLegacyEtlService;

    public PaymentsLegacyEtlController(PaymentsLegacyEtlService paymentsLegacyEtlService) {
        this.paymentsLegacyEtlService = paymentsLegacyEtlService;
    }

    @PostMapping("/run")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentsLegacyEtlResult> runEtl(
            @RequestParam("sourceCode") String sourceCode,
            @RequestParam(name = "batchSize", defaultValue = "500") int batchSize
    ) {
        PaymentsLegacyEtlResult result = paymentsLegacyEtlService.run(sourceCode, batchSize);
        return ResponseEntity.ok(result);
    }
}
