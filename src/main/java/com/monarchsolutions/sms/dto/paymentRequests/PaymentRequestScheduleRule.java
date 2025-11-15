package com.monarchsolutions.sms.dto.paymentRequests;

import java.time.LocalDate;

public class PaymentRequestScheduleRule {

    private Long paymentRequestScheduledId;
    private Long schoolId;
    private Long groupId;
    private Long studentId;
    private String payload;
    private LocalDate nextDueDate;
    private Integer periodOfTimeId;
    private Integer intervalCount;
    private LocalDate endDate;
    private Long createdBy;

    public Long getPaymentRequestScheduledId() {
        return paymentRequestScheduledId;
    }

    public void setPaymentRequestScheduledId(Long paymentRequestScheduledId) {
        this.paymentRequestScheduledId = paymentRequestScheduledId;
    }

    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public LocalDate getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(LocalDate nextDueDate) {
        this.nextDueDate = nextDueDate;
    }

    public Integer getPeriodOfTimeId() {
        return periodOfTimeId;
    }

    public void setPeriodOfTimeId(Integer periodOfTimeId) {
        this.periodOfTimeId = periodOfTimeId;
    }

    public Integer getIntervalCount() {
        return intervalCount;
    }

    public void setIntervalCount(Integer intervalCount) {
        this.intervalCount = intervalCount;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
}
