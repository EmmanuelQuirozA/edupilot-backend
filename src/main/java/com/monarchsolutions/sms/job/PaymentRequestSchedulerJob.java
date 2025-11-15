package com.monarchsolutions.sms.job;

import java.time.LocalDate;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.monarchsolutions.sms.service.PaymentRequestSchedulerService;

@Component
public class PaymentRequestSchedulerJob {

    private final PaymentRequestSchedulerService schedulerService;

    public PaymentRequestSchedulerJob(PaymentRequestSchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    @Scheduled(cron = "${jobs.payment-request-scheduler.cron:0 15 2 * * *}")
    public void runDailyJob() {
        schedulerService.execute(LocalDate.now());
    }
}
